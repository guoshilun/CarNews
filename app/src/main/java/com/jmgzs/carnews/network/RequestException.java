package com.jmgzs.carnews.network;

/**网络请求相关异常
 * Created by wxl on 2017/6/7.
 */

public class RequestException extends RuntimeException {
    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
