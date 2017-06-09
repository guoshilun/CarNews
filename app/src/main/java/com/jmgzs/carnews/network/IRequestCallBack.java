package com.jmgzs.carnews.network;

/**请求回调
 * Created by wxl on 2017/6/7.
 */

public interface IRequestCallBack<T> {

    Class<T> getType();

    void onSuccess(String url, T data);

    void onFailure(String url, int errorCode, String msg);

    void onCancel(String url);
}
