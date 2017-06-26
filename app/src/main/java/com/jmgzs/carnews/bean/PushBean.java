package com.jmgzs.carnews.bean;

/**
 * Created by Wxl on 2017/6/22.
 */

public class PushBean extends BaseInfo{

    /**
     * aid : 45296
     * abstr : 我叫林雨，今年34岁，是一家私企老板。我生长在单亲家庭，小时候家境贫困。我知道白手起家的辛酸和无奈。所以，从高中时代开始，我就十分渴望能拥有一份纯洁的爱情，和一个温暖的家。为了这个卑微的梦想，我竭尽全力奋斗，抛开一切繁华
     * img : http://mjcrawl-1252328573.file.myqcloud.com/fa9d23a764871c03e4921f6e116aa4a95bcd53c6.jpg
     * title : 突然发现老婆的秘密我想离婚
     */

    private int aid;
    private String abstr;
    private String img;
    private String title;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getAbstr() {
        return abstr;
    }

    public void setAbstr(String abstr) {
        this.abstr = abstr;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
