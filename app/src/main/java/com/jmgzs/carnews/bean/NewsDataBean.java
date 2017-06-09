package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class NewsDataBean extends BaseInfo {

    private String title = "Default";

    public NewsDataBean(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
