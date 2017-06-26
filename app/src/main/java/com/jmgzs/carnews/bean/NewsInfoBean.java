package com.jmgzs.carnews.bean;

import java.util.ArrayList;

/**
 * 新闻详情返回
 * Created by Wxl on 2017/6/21.
 */

public class NewsInfoBean extends BaseInfo implements IGetRsp {

    private ArrayList<NewsDataBean> data;
    private StatusBean rsp;

    public ArrayList<NewsDataBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsDataBean> data) {
        this.data = data;
    }

    public void setRsp(StatusBean rsp) {
        this.rsp = rsp;
    }

    @Override
    public StatusBean getRsp() {
        return rsp;
    }
}
