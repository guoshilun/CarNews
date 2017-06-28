package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/27.
 * Description:
 */

public class UpdateInfo extends BaseInfo {

    private String version_name;
    private String url;
    private String msg;

    private int force;
    private StatusBean rsp;

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

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

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public StatusBean getRsp() {
        return rsp;
    }

    public void setRsp(StatusBean rsp) {
        this.rsp = rsp;
    }
}
