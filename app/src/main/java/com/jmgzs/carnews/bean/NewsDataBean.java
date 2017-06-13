package com.jmgzs.carnews.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class NewsDataBean extends BaseInfo {

    private String title = "Default";

    private List<Photo> photo;

    public NewsDataBean(String title) {
        this(title, null);
    }

    public NewsDataBean(String title, List<Photo> photos) {
        this.title = title;
        if (photos == null || photos.size() == 0) {
            photos = new ArrayList<>();
            Photo p = new Photo();
            photos.add(p);
        }
        this.photo = photos;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<Photo> getPhoto() {
        return photo;
    }
}
