package com.jmgzs.carnews.network;

/**请求回调
 * Created by wxl on 2017/6/7.
 */

public interface IRequestCallBack {
    void onSuccess(String url, byte[] data);

    void onFailure(String url, int errorCode, String msg, byte[] response);

    void onCancel(String url);

    void onFinish(String url);
}
