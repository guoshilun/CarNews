package com.jmgzs.carnews.network;

/**
 * Created by wxl on 2017/6/7.
 */

public enum  NetworkErrorCode {
    ERROR_CODE_UNKNOWN(101, "未知异常"), ERROR_CODE_EMPTY_RESPONSE(102, "返回数据为空"), ERROR_CODE_NO_NETWORK(103, "网络异常"), ERROR_CODE_DATA_FORMAT(104, "数据格式异常"), ERROR_CODE_TIMEOUT(105, "服务器连接超时");

    private final int code;
    private final String msg;

    NetworkErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static NetworkErrorCode mapOkhttp(int errorCode){
        switch (errorCode){
            default:
                return ERROR_CODE_UNKNOWN;
        }
    }
}
