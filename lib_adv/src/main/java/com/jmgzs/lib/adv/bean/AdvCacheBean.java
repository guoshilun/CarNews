package com.jmgzs.lib.adv.bean;

/**
 * Created by Wxl on 2017/7/18.
 */

public class AdvCacheBean {

    private String html;
    private AdvResponseBean response;
    private String filePath;

    public AdvCacheBean(String html, AdvResponseBean response, String filePath) {
        this.html = html;
        this.response = response;
        this.filePath = filePath;
    }

    public AdvCacheBean(AdvResponseBean response) {
        this.response = response;
    }

    public AdvCacheBean() {

    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public AdvResponseBean getResponse() {
        return response;
    }

    public void setResponse(AdvResponseBean response) {
        this.response = response;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
