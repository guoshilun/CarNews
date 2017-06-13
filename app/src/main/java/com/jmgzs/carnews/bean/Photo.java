package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class Photo extends BaseInfo {

    private String url = "http://img1.gtimg.com/news/pics/hv1/135/102/2216/144121545.jpg";


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }
}
