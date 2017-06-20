package com.jmgzs.lib_network.network;

/**请求回调
 * Created by wxl on 2017/6/7.
 */

public interface IRequestCallBack<T> {

    void onSuccess(String url, T data);

    void onFailure(String url, int errorCode, String msg);

    void onCancel(String url);
}
