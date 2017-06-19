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
import com.jmgzs.carnews.network.annotation.JsonElement.JsonNotInvalidException;
import com.jmgzs.carnews.network.annotation.JsonElementHelper;
import com.jmgzs.carnews.util.L;
import com.jmgzs.carnews.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
    private static final int DEFAULT_TIMEOUT = 15;

    private Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.retryOnConnectionFailure(true);
        httpClientBuilder.addInterceptor(interceptor);

        //绕过HTTPS验证
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        httpClientBuilder
                .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                .hostnameVerifier(DO_NOT_VERIFY);

        //生成Retrofit对象
        retrofit = new Retrofit.Builder()
                .baseUrl(Urls.BASE_URL)
                .client(httpClientBuilder.build())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    public <T> void get(Context context, boolean isSync, IRxRequest request, final Class<T> responseType, IRequestCallBack<T> callback) {
        requestNetwork(true, context, isSync, request, responseType, callback);
    }


    public <T> void post(Context context, boolean isSync, IRxRequest request, Class<T> responseType, IRequestCallBack<T> callback) {
        requestNetwork(false, context, isSync, request, responseType, callback);
    }
    public <T> void requestNetwork(final boolean isGet, final Context context, final IRxRequest request, final Class<T> responseType, final IRequestCallBack<T> callback) {
        requestNetwork(isGet, context, false, request, responseType, callback);
    }


    public <T> void requestNetwork(final boolean isGet, final Context context, final boolean isSync, final IRxRequest request, final Class<T> responseType, final IRequestCallBack<T> callback) {
        IHttpRequest httpRequest = getRetrofit().create(IHttpRequest.class);
        Observable<ResponseBody> observable;
        if (isGet){
            observable = httpRequest.get(request.getUrl(), request.getHeaders() == null ? new HashMap<String, String>() : request.getHeaders());
        }else{
            observable = httpRequest.post(request.getUrl(), RequestBody.create(MediaType.parse("application/json"), request.getBody() == null ? "" : request.getBody()), request.getHeaders() == null ? new HashMap<String, String>() : request.getHeaders());
        }
        L.e("请求的URL："+request.getUrl());
        final String httpType = isGet ? "Get" : "Post";
        if (isSync){
            observable = observable
                    .subscribeOn(ImmediateThinScheduler.INSTANCE)
                    .observeOn(ImmediateThinScheduler.INSTANCE);
        }else{
            observable = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        observable
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody data) throws Exception {
                        String url = request.getUrl();
                        String json = data.string();
                        L.e(httpType + " url:" + url + " 返回的数据：" + json);
                        //解析json数据
                        parseData(isGet, json, request, responseType, callback);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        L.e(httpType + " Error：" + throwable, throwable);
                        if (callback != null) {
                            int errorCode;
                            String msg;
                            if (throwable instanceof SocketTimeoutException) {
                                errorCode = NetworkErrorCode.ERROR_CODE_TIMEOUT.getCode();
                                msg = NetworkErrorCode.ERROR_CODE_TIMEOUT.getMsg();
                            }else if (throwable instanceof ConnectException){
                                if (NetworkUtils.hasNetwork(context)){
                                    errorCode = NetworkErrorCode.ERROR_CODE_SERVER_CONNECT_ERROR.getCode();
                                    msg = NetworkErrorCode.ERROR_CODE_SERVER_CONNECT_ERROR.getMsg();
                                }else{
                                    errorCode = NetworkErrorCode.ERROR_CODE_NO_NETWORK.getCode();
                                    msg = NetworkErrorCode.ERROR_CODE_NO_NETWORK.getMsg();
                                }
                            }else if (throwable instanceof HttpException){
                                HttpException ex = (HttpException) throwable;
                                L.e(ex.toString());
                                errorCode = NetworkErrorCode.ERROR_CODE_SERVER_ERROR.getCode();
                                msg = ex.code() + " " + NetworkErrorCode.ERROR_CODE_SERVER_ERROR.getMsg();
                            }
                            else {
                                errorCode = NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode();
                                msg = NetworkErrorCode.ERROR_CODE_UNKNOWN.getMsg();
                            }
                            L.e(httpType + " ErrorCode：" + errorCode + " Message:" + msg);
                            callback.onFailure(request.getUrl(), errorCode, msg);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        L.e(httpType + " Completed！");
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        L.e(httpType + " disposed！");
                    }
                });
    }

    private <T> boolean parseData(boolean isGet, String data, final IRxRequest request, final Class<T> responseType, final IRequestCallBack<T> callback) {
        if (callback == null) {
            return false;
        }
        final String requestType = isGet ? "Get" : "Post";
        try {
            // 解析返回是否正常
            L.e("data:"+data);
            JSONObject json = new JSONObject(data);
            JSONObject rsp = json.getJSONObject("rsp");
            int code = rsp.getInt("status");
            if (code == 0){
                //解析返回的数据
//                JSONObject dataObj = json.getJSONObject("data");
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new JsonFilterAdapterFactory()).create();
                T result = gson.fromJson(data, responseType);
                callback.onSuccess(request.getUrl(), result);
                return true;
            }else{
                String msg = json.getString("reason");
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
                                L.e("JsonElement:"+item);
                                Object value;
                                if (item == null){
                                    value = null;
                                }else if (item.isJsonNull()){
                                    value = null;
                                    L.e("Null");
                                }else if (item.isJsonArray()){
                                    value = item.getAsJsonArray();
                                    L.e("Array:"+value);
                                }else if (item.isJsonObject()){
                                    value = item.getAsJsonObject();
                                    L.e("Object:"+value);
                                }else if (item.isJsonPrimitive()){
                                    JsonPrimitive primitive = item.getAsJsonPrimitive();
                                    L.e("Primitive:"+primitive);
                                    if (primitive.isBoolean()){
                                        value = primitive.getAsBoolean();
                                    }else if (primitive.isString()){
                                        value = primitive.getAsString();
                                    }else {
                                        Number number = primitive.getAsNumber();
                                        L.e("Number:"+number);
                                        value = number.doubleValue();
                                    }
                                }else{
                                    value = null;
                                }
                                L.e("field:"+field+"\tvalue:"+value+"\ttarget:"+obj);
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
