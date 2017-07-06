package com.jmgzs.carnews.bean;

/**
 * Created by mac on 17/6/30.
 * Description:
 */

public class AdvDataBean extends NewsDataBean {

    private int advW;
    private int advH;
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getAdvW() {
        return advW;
    }

    public void setAdvW(int advW) {
        this.advW = advW;
    }

    public int getAdvH() {
        return advH;
    }

    public void setAdvH(int advH) {
        this.advH = advH;
    }

    @Override
    public EnumItemType getItemType() {
        return EnumItemType.ADV;
    }
}
