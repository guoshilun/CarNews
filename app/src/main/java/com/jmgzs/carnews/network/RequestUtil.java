package com.jmgzs.carnews.network;

import java.util.Map;

/**网络请求工具类
 * Created by wxl on 2017/6/7.
 */

public class RequestUtil {

    private static final String POST = "post";
    private static final String GET = "get";

    public static void requestByPost(String url, String content, IRequestCallBack callBack){
        requestByPost(url, content, null, callBack);
    }

    public static void requestByPost(String url, String content, Map<String, String> headers, IRequestCallBack callBack){
        request(url, POST, content.getBytes(), headers, callBack);
    }

    public static void requestByGet(String url, IRequestCallBack callBack){
        requestByGet(url, null, callBack);
    }

    public static void requestByGet(String url, Map<String, String> headers, IRequestCallBack callBack){
        request(url, GET, null, headers, callBack);
    }

    private static void request(String url, String requestType, byte[] body, Map<String, String> headers, IRequestCallBack callBack){
        if (POST.equals(requestType)){

        }else if (GET.equals(requestType)){

        }else{
            throw new RequestException("请求类型不支持");
        }

    }
}
