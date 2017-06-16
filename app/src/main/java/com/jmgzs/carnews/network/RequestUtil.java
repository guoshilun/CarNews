package com.jmgzs.carnews.network;

import android.content.Context;

import com.jmgzs.carnews.network.retrofit.RetrofitRequestManager;

import java.util.Map;

/**网络请求工具类
 * Created by wxl on 2017/6/7.
 */

public class RequestUtil {

    private static final String POST = "post";
    private static final String GET = "get";

    public static <T> void requestByPostSync(Context context, String url, String content, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByPostSync(context, url, false, content, null, responseType, callBack);
    }
    public static <T> void requestByPostAsy(Context context, String url, String content, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByPostAsy(context, url, false, content, null, responseType, callBack);
    }
    public static <T> void requestByPostSync(Context context, String url, boolean isUseCache, String content, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByPostSync(context, url, isUseCache, content, null, responseType, callBack);
    }
    public static <T> void requestByPostAsy(Context context, String url, boolean isUseCache, String content, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByPostAsy(context, url, isUseCache, content, null, responseType, callBack);
    }
    public static <T> void requestByPostSync(Context context, String url, boolean isUseCache, String content, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, isUseCache, true, POST, content == null ? new byte[0] : content.getBytes(), headers, responseType, callBack);
    }
    public static <T> void requestByPostAsy(Context context, String url, boolean isUseCache, String content, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, isUseCache, false, POST, content == null ? new byte[0] : content.getBytes(), headers, responseType, callBack);
    }
    public static <T> void requestByGetSync(Context context, String url, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByGetSync(context, url, false, null, responseType, callBack);
    }
    public static<T>  void requestByGetAsy(Context context, String url, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByGetAsy(context, url, false, null, responseType, callBack);
    }
    public static <T> void requestByGetSync(Context context, String url, boolean isUseCache, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByGetSync(context, url, isUseCache, null, responseType, callBack);
    }
    public static<T>  void requestByGetAsy(Context context, String url, boolean isUseCache, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByGetAsy(context, url, isUseCache, null, responseType, callBack);
    }
    public static <T> void requestByGetSync(Context context, String url, boolean isUseCache, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, isUseCache, true, GET, null, headers, responseType, callBack);
    }
    public static <T> void requestByGetAsy(Context context, String url, boolean isUseCache, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, isUseCache, false, GET, null, headers, responseType, callBack);
    }

    private static <T> void request(Context context, final String url, boolean isUseCache, boolean isSync, String requestType, final byte[] body, final Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        IRxRequest req = new IRxRequest(){
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public String getBody() {
                return body == null ? null : new String(body);
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        if (POST.equals(requestType)){
            RetrofitRequestManager.get().post(context, isUseCache, isSync, req, responseType, callBack);
        }else if (GET.equals(requestType)){
            RetrofitRequestManager.get().get(context, isUseCache, isSync, req, responseType, callBack);
        }else{
            throw new RequestException("请求类型不支持");
        }

    }
}
