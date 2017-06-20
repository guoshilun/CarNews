package com.jmgzs.carnews.bean;

import java.util.ArrayList;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class NewsListBean extends BaseInfo {

    private ArrayList<NewsDataBean> data;

    private StatusBean rsp;

    private int next_key;

    public ArrayList<NewsDataBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsDataBean> data) {
        this.data = data;
    }

    public StatusBean getRsp() {
        return rsp;
    }

    public void setRsp(StatusBean rsp) {
        this.rsp = rsp;
    }

    public int getNext_key() {
        return next_key;
    }

    public void setNext_key(int next_key) {
        this.next_key = next_key;
    }
}
