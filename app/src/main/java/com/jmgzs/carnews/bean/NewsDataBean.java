package com.jmgzs.carnews.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class NewsDataBean extends BaseInfo {

    private String title = "Default";
    private String aid = "";
    private String publish_time = "";
    private String publish_source = "";
    private String summary = "";

    private ArrayList<Photo> img_list;

    public NewsDataBean(){
        this("");
    }

    public NewsDataBean(String title) {
        this(title, null);
    }

    public NewsDataBean(String title, ArrayList<Photo> photos) {
        this.title = title;
        if (photos == null || photos.size() == 0) {
            photos = new ArrayList<>();
            Photo p = new Photo();
            photos.add(p);
        }
        this.img_list = photos;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public ArrayList<Photo> getImg_list() {
        return img_list;
    }

    public void setImg_list(ArrayList<Photo> img_list) {
        this.img_list = img_list;
    }
}
