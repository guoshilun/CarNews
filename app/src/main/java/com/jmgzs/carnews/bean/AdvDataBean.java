package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/30.
 * Description:
 */

public class AdvDataBean extends NewsDataBean {

    private String html;
    private String file;//html文件
    private String landingPageUrl;//landingPage
    private int adType;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public String getLandingPageUrl() {
        return landingPageUrl;
    }

    public void setLandingPageUrl(String landingPageUrl) {
        this.landingPageUrl = landingPageUrl;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    @Override
    public EnumItemType getItemType() {
        return EnumItemType.ADV;
    }
}
