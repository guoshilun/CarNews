package com.jmgzs.lib_network.network;

import android.content.Context;

import com.jmgzs.lib_network.network.retrofit.RetrofitRequestManager;

import java.util.Map;

/**网络请求工具类
 * Created by wxl on 2017/6/7.
 */

public class RequestUtil {

    private static final String POST = "post";
    private static final String GET = "get";

    public static <T> void requestByPostSync(Context context, String url, String content, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByPostSync(context, url, content, null, responseType, callBack);
    }
    public static <T> void requestByPostAsy(Context context, String url, String content, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByPostAsy(context, url, content, null, responseType, callBack);
    }
    public static <T> void requestByPostSync(Context context, String url, String content, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, true, POST, content == null ? new byte[0] : content.getBytes(), headers, responseType, callBack);
    }
    public static <T> void requestByPostAsy(Context context, String url, String content, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url,false, POST, content == null ? new byte[0] : content.getBytes(), headers, responseType, callBack);
    }
    public static <T> void requestByGetSync(Context context, String url, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByGetSync(context, url, null, responseType, callBack);
    }
    public static<T>  void requestByGetAsy(Context context, String url, Class<T> responseType, IRequestCallBack<T> callBack){
        requestByGetAsy(context, url, null, responseType, callBack);
    }
    public static <T> void requestByGetSync(Context context, String url, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, true, GET, null, headers, responseType, callBack);
    }
    public static <T> void requestByGetAsy(Context context, String url, Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
        request(context, url, false, GET, null, headers, responseType, callBack);
    }

    private static <T> void request(Context context, final String url, boolean isSync, String requestType, final byte[] body, final Map<String, String> headers, Class<T> responseType, IRequestCallBack<T> callBack){
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
            RetrofitRequestManager.get().post(context, isSync, req, responseType, callBack);
        }else if (GET.equals(requestType)){
            RetrofitRequestManager.get().get(context, isSync, req, responseType, callBack);
        }else{
            throw new RequestException("请求类型不支持");
        }

    }
}
