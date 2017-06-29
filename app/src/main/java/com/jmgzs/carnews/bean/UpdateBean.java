package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/29.
 * Description:
 */

public class UpdateBean extends BaseInfo {

    private UpdateInfo data;
    private StatusBean rsp;

    public UpdateInfo getData() {
        return data;
    }

    public void setData(UpdateInfo data) {
        this.data = data;
    }

    public StatusBean getRsp() {
        return rsp;
    }

    public void setRsp(StatusBean rsp) {
        this.rsp = rsp;
    }
}
