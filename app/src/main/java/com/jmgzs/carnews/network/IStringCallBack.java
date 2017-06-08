package com.jmgzs.carnews.network;

import com.jmgzs.carnews.util.L;

import java.util.Arrays;

/**字符串返回回调
 * Created by wxl on 2017/6/7.
 */

public abstract class IStringCallBack implements IRequestCallBack {
    @Override
    public void onSuccess(String url, byte[] data) {
        if (data == null){
            onFailure(url, NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getCode(), NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getMsg(), (byte[])null);
            return;
        }
        try {
            onSuccess(url, new String(data));
        } catch (Exception e) {
            L.e("String 格式异常", e);
            onFailure(url, NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode(), NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getMsg(), data);
        }
    }

    public abstract void onSuccess(String url, String data);

    public abstract void onFailure(String url, int errorCode, String msg, String response);

    @Override
    public void onFailure(String url, int errorCode, String msg, byte[] response) {
        if (errorCode == NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode()){
            L.e("error byte response："+ Arrays.toString(response));
            response = null;
        }
        if (response == null){
            onFailure(url, errorCode, msg, (String)null);
        }else{
            try {
                onFailure(url, errorCode, msg, new String(response));
            } catch (Exception e) {
                L.e("String 格式异常", e);
                onFailure(url, errorCode, msg, (String)null);
            }
        }
    }
}
