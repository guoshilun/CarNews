package com.jmgzs.carnews.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class NewsDataBean extends BaseInfo implements IItemType {

    private String title = "Default";
    private int aid = 0;
    private String publish_time = "";
    private String publish_source = "";
    private String abstr = "";
    private int search_key = 0;
    private String content;
    private int del = 0;//本地记录1删除,0正常

    private List<String> img_list;

    public NewsDataBean() {
        this("");
    }

    public NewsDataBean(String title) {
        this(title, null);
    }

    public NewsDataBean(String title, ArrayList<String> photos) {
        this.title = title;
        if (photos == null || photos.size() == 0) {
            photos = new ArrayList<>();
            Photo p = new Photo();
            photos.add(p.getUrl());
        }
        this.img_list = photos;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getPublish_source() {
        return publish_source;
    }

    public void setPublish_source(String publish_source) {
        this.publish_source = publish_source;
    }

    public String getAbstr() {
        return abstr;
    }

    public void setAbstr(String abstr) {
        this.abstr = abstr;
    }

    public int getSearch_key() {
        return search_key;
    }

    public void setSearch_key(int search_key) {
        this.search_key = search_key;
    }

    public List<String> getImg_list() {
        return img_list;
    }

    public void setImg_list(List<String> img_list) {
        this.img_list = img_list;
    }

    @Override
    public EnumItemType getItemType() {
        return img_list == null || img_list.size() < 3 ? EnumItemType.IMAGE : EnumItemType.IMAGE3;
    }
}
