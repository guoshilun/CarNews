package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class StatusBean extends BaseInfo {

    private int status =0 ;
    private String reason="";

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
