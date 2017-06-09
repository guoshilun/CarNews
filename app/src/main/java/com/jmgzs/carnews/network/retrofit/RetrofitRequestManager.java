package com.jmgzs.carnews.network.retrofit;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jmgzs.carnews.network.ConfigCache;
import com.jmgzs.carnews.network.IRequestCallBack;
import com.jmgzs.carnews.network.IRxRequest;
import com.jmgzs.carnews.network.NetworkErrorCode;
import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.network.annotation.JsonElementHelper;
import com.jmgzs.carnews.util.L;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jmgzs.carnews.network.annotation.JsonElement.JsonNotInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/**Retrofit请求工具类
 * Created by XJ on 2016/9/9.
 */
public class RetrofitRequestManager {

    private static class Singleton {
        private static final RetrofitRequestManager manager = new RetrofitRequestManager();
    }

    private RetrofitRequestManager() {
    }

    public static RetrofitRequestManager get() {
        return Singleton.manager;
    }

    private Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.retryOnConnectionFailure(true);
        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.build();

        retrofit = new Retrofit.Builder().baseUrl(Urls.BASE_URL)
                .client(httpClientBuilder.build())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    public <T> void get(Context context, boolean useCache, IRxRequest request, IRequestCallBack<T> callback) {
        String url = Urls.BASE_URL + request.getUrl();
        if (useCache) {
            String cache = ConfigCache.getUrlCache(context, url);
            if (!TextUtils.isEmpty(cache)) {
                parseData(true, cache, request, callback);
                return;
            }
        }
        requestNetwork(true, context, useCache, request, callback);
    }


    public <T> void post(Context context, boolean useCache, IRxRequest request, IRequestCallBack<T> callback) {
        String url = Urls.BASE_URL + request.getUrl();
        if (useCache) {
            String cache = ConfigCache.getUrlCache(context, url);
            if (!TextUtils.isEmpty(cache)) {
                parseData(false, cache, request, callback);
                return;
            }
        }
        requestNetwork(false, context, useCache, request, callback);
    }

    public <T> void requestNetwork(final boolean isGet, final Context context, final boolean saveCache, final IRxRequest request, final IRequestCallBack<T> callback) {
        IHttpRequest httpRequest = getRetrofit().create(IHttpRequest.class);
        Observable<ResponseBody> observable;
        if (isGet){
            observable = httpRequest.get(request.getUrl(), request.getHeaders() == null ? new HashMap<String, String>() : request.getHeaders());
        }else{
            observable = httpRequest.post(request.getUrl(), request.getBody(), request.getHeaders() == null ? new HashMap<String, String>() : request.getHeaders());
        }
        final String requestType = isGet ? "Get" : "Post";
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody data) throws Exception {
                        String url = Urls.BASE_URL + request.getUrl();
                        String json = data.string();
                        L.e(requestType + " url:" + url + " 返回的数据：" + json);
                        //解析json数据
                        if (parseData(isGet, json, request, callback)) {
                            if (saveCache) {
                                ConfigCache.setUrlCache(context, url, json);
                            }
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        L.e(requestType + " Error：" + throwable, throwable);
                        if (callback != null) {
                            int errorCode;
                            String msg;
                            if (throwable instanceof SocketTimeoutException) {
                                errorCode = NetworkErrorCode.ERROR_CODE_TIMEOUT.getCode();
                                msg = NetworkErrorCode.ERROR_CODE_TIMEOUT.getMsg();
                            } else {
                                errorCode = NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode();
                                msg = NetworkErrorCode.ERROR_CODE_UNKNOWN.getMsg();
                            }
                            L.e(requestType + " ErrorCode：" + errorCode + " Message:" + msg);
                            callback.onFailure(request.getUrl(), errorCode, msg);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        L.e(requestType + " Completed！");
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        L.e(requestType + " disposed！");
                    }
                });
    }

    private <T> boolean parseData(boolean isGet, String data, final IRxRequest request, final IRequestCallBack<T> callback) {
        if (callback == null) {
            return false;
        }
        final String requestType = isGet ? "Get" : "Post";
        try {
            // 解析返回是否正常
            L.e("data:"+data);
            JSONObject json = new JSONObject(data);
            int code = json.getInt("errorCode");
            if (code == 0){
                //解析返回的数据
                JSONObject dataObj = json.getJSONObject("data");
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new JsonFilterAdapterFactory()).create();
                T result = gson.fromJson(dataObj.toString(), callback.getType());
                callback.onSuccess(request.getUrl(), result);
                return true;
            }else{
                String msg = json.getString("message");
                L.e(requestType + " ErrorCode：" + code + " Message:" + msg);
                callback.onFailure(request.getUrl(), code, msg);
                return false;
            }
        } catch (JsonSyntaxException e) {
            L.e(requestType + " Error：" + e, e);
            int errorCode = NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode();
            String msg = NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getMsg();
            L.e(requestType + " ErrorCode：" + errorCode + " Message:" + msg);
            callback.onFailure(request.getUrl(), errorCode, msg);
            return false;
        } catch (JSONException e) {
            L.e(requestType + " Error：" + e, e);
            int errorCode = NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode();
            String msg = NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getMsg();
            L.e(requestType + " ErrorCode：" + errorCode + " Message:" + msg);
            callback.onFailure(request.getUrl(), errorCode, msg);
            return false;
        }
    }

}
class JsonFilterAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementTypeAdapter = gson.getAdapter(JsonElement.class);


        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }
            //当获取了http请求的数据后，获得data所对应的值
            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = elementTypeAdapter.read(in);
                if (jsonElement.isJsonArray()){
                    return delegate.fromJsonTree(jsonElement);
                }else{
                    T obj = delegate.fromJsonTree(jsonElement);
                    if (jsonElement.isJsonObject()){
                        Class<?> clz = (Class<?>) type.getType();
                        Field[] fields = clz.getDeclaredFields();
                        JsonObject jo = jsonElement.getAsJsonObject();
                        for (Field field : fields){
                            try {
                                field.setAccessible(true);
                                JsonElement item = jo.get(field.getName());
                                Object value;
                                if (item == null){
                                    value = null;
                                }else if (item.isJsonNull()){
                                    value = null;
                                }else if (item.isJsonArray()){
                                    value = item.getAsJsonArray();
                                }else if (item.isJsonObject()){
                                    value = item.getAsJsonObject();
                                }else if (item.isJsonPrimitive()){
                                    JsonPrimitive primitive = item.getAsJsonPrimitive();
                                    if (primitive.isBoolean()){
                                        value = primitive.getAsBoolean();
                                    }else if (primitive.isString()){
                                        value = primitive.getAsString();
                                    }else {
                                        Number number = primitive.getAsNumber();
                                        if (short.class.isInstance(number)){
                                            value = primitive.getAsShort();
                                        }else if (int.class.isInstance(number)){
                                            value = primitive.getAsInt();
                                        }else if (float.class.isInstance(number)){
                                            value = primitive.getAsFloat();
                                        }else if (double.class.isInstance(number)){
                                            value = primitive.getAsDouble();
                                        }else if (long.class.isInstance(number)){
                                            value = primitive.getAsLong();
                                        }else if (byte.class.isInstance(number)){
                                            value = primitive.getAsByte();
                                        }else if (char.class.isInstance(number)){
                                            value = primitive.getAsCharacter();
                                        }else{
                                            value = null;
                                        }
                                    }
                                }else{
                                    value = null;
                                }
                                JsonElementHelper.checkJsonValidation(field, value, obj);
                            } catch (JsonNotInvalidException e) {
                                throw new JsonSyntaxException(e);
                            }
                        }
                    }
                    return obj;
                }
            }

        }.nullSafe();
    }
}
