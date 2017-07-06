package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/27.
 * Description:
 */

public class UpdateInfo extends BaseInfo {

    private int version = 0;
    private String url = "";
    private String msg = "test";

    private boolean force = false;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
