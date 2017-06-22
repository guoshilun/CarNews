package com.jmgzs.carnews.util;

import com.jmgzs.carnews.bean.IGetRsp;
import com.jmgzs.carnews.bean.StatusBean;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.NetworkErrorCode;

/**返回错误预处理
 * Created by Wxl on 2017/6/20.
 */

public class ResUtils {

    public static boolean processResponse(String url, Object response, IRequestCallBack<?> callBack){
        if (response instanceof IGetRsp){
            if (callBack != null){
                IGetRsp res = (IGetRsp) response;
                StatusBean rsp;
                if ((rsp = res.getRsp()) == null || rsp.getStatus() != 1){
                    int errorCode;
                    String msg;
                    if (rsp == null){
                        errorCode = NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getCode();
                        msg = NetworkErrorCode.ERROR_CODE_DATA_FORMAT.getMsg();
                    }else{
                        errorCode = rsp.getStatus();
                        msg = rsp.getReason();
                    }
                    callBack.onFailure(url, errorCode, msg);
                    return false;
                }else{
                    return true;
                }
            }else{
                return true;
            }
        }else{
            return true;
        }
    }
}
