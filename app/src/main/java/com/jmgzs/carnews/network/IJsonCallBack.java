package com.jmgzs.carnews.network;

import com.jmgzs.carnews.util.L;

import org.json.JSONException;
import org.json.JSONObject;

/**json的返回回调
 * Created by wxl on 2017/6/7.
 */

public abstract class IJsonCallBack extends IStringCallBack{
    @Override
    public void onSuccess(String url, String data) {
        try {
            onSuccess(url, new JSONObject(data));
        } catch (JSONException e) {
            L.e("json 格式异常", e);
            onFailure(url, NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode(), NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getMsg(), (String)null);
        }
    }

    public abstract void onSuccess(String url, JSONObject json);

    public abstract void onFailure(String url, int errorCode, String msg, JSONObject response);

    @Override
    public void onFailure(String url, int errorCode, String msg, String response) {
        if (errorCode == NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode()){
            response = null;
        }
        if (response == null){
            onFailure(url, errorCode, msg, (String)null);
        }else{
            try {
                onFailure(url, errorCode, msg, new JSONObject(response));
            } catch (JSONException e) {
                L.e("json 格式异常", e);
                onFailure(url, errorCode, msg, (String)null);
            }
        }

    }
}
