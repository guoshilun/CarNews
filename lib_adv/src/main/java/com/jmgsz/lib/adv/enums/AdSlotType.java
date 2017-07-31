package com.jmgsz.lib.adv.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 应用对应的广告位类型
 * Created by Wxl on 2017/6/20.
 */

public enum AdSlotType {
    OPEN_640_960(0, 640, 960, 640, 2033), OPEN_640_960_W(0, 640, 960, 640, 2071), OPEN_1182_1620(0, 1182, 1620, 1182, 2030), OPEN_720_1066(0, 720, 1066, 720, 2031), OPEN_480_658(0, 480, 658, 480, 2032), OPEN_640_1136(0, 640, 1136, 640, 2024), OPEN_640_1136_W(0, 640, 1136, 640, 2072),
    INFO_720_405_W(1, 720, 489, 720, 405, 720, 2039), INFO_800_120_W(1, 800, 120, 45, 45, 800, 2029), INFO_600_300_W(1, 600, 300, 600, 2010),
    BANNER_640_100(2, 640, 100, 640, 2044), BANNER_800_120(2, 800, 120, 800, 2017), BANNER_640_200(2, 640, 200, 640, 2018), BANNER_440_160(2, 440, 160, 440, 2019), BANNER_240_180_W(2, 240, 180, 240, 2003), BANNER_1000_500_W(2, 1000, 500, 1000, 2061), BANNER_640_100_W(2, 640, 7, 0, 0, 37, 37, 640, 2001),
    INSERT_600_500(3, 600, 500, 720, 2020), INSERT_600_600(3, 600, 600, 720, 2021), INSERT_640_960(3, 640, 960, 720, 2022), INSERT_480_800(3, 480, 800, 640, 2023), INSERT_80_80_W(3, 1014, 837, 0, 0, 80, 80, 1080, 2004);

    private static List<AdSlotType> openList;
    private static List<AdSlotType> infoList;
    private static List<AdSlotType> bannerList;
    private static List<AdSlotType> insertList;
    private static volatile List<Integer> typeList;

    public static final int TYPE_OPEN = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_BANNER = 2;
    public static final int TYPE_INSERT = 3;

    static {
        openList = new ArrayList<>();
        infoList = new ArrayList<>();
        bannerList = new ArrayList<>();
        insertList = new ArrayList<>();
        for (AdSlotType type : AdSlotType.values()) {
            switch (type.type) {
                case TYPE_OPEN:
                    openList.add(type);
                    break;
                case TYPE_INFO:
                    infoList.add(type);
                    break;
                case TYPE_BANNER:
                    bannerList.add(type);
                    break;
                case TYPE_INSERT:
                    insertList.add(type);
                    break;
            }
        }
    }

    private int type;//0为开屏，1为信息流，2为横幅，3为插屏
    private int standardWidth;//基准设备宽
    private int width;
    private int height;
    private int templateId;
    private int imgW;
    private int imgH;
    private int iconW;
    private int iconH;

    /**
     * 包含一张大图
     *
     * @param type
     * @param width
     * @param height
     * @param imgW
     * @param imgH
     * @param templateId
     */
    AdSlotType(int type, int width, int height, int imgW, int imgH, int standardWidth, int templateId) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.imgW = imgW;
        this.imgH = imgH;
        this.standardWidth = standardWidth;
        this.templateId = templateId;
    }

    /**
     * 纯图
     *
     * @param type
     * @param width
     * @param height
     * @param standardWidth
     * @param templateId
     */
    AdSlotType(int type, int width, int height, int standardWidth, int templateId) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.imgW = width;
        this.imgH = height;
        this.standardWidth = standardWidth;
        this.templateId = templateId;
    }

    /**
     * 包含icon，大图等的图文
     *
     * @param type
     * @param width
     * @param height
     * @param imgW
     * @param imgH
     * @param iconW
     * @param iconH
     * @param standardWidth
     * @param templateId
     */
    AdSlotType(int type, int width, int height, int imgW, int imgH, int iconW, int iconH, int standardWidth, int templateId) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.imgW = imgW;
        this.imgH = imgH;
        this.iconW = iconW;
        this.iconH = iconH;
        this.standardWidth = standardWidth;
        this.templateId = templateId;
    }

    public static AdSlotType getAdSlotTypeByTemplateId(int templateId) {
        for (AdSlotType type : AdSlotType.values()) {
            if (type.templateId == templateId) {
                return type;
            }
        }
        return null;
    }

    public static AdSlotType getRandomOpenScreenType() {
        Random random = new Random();

        int position = random.nextInt(openList.size());
        return openList.get(position);
    }

    public static AdSlotType getRandomInsertType() {
        Random random = new Random();

        int position = random.nextInt(insertList.size());
        return insertList.get(position);
    }

    public synchronized static List<Integer> getAdTypeList() {
        if (typeList == null) {
            typeList = new ArrayList<>();
            typeList.add(TYPE_OPEN);
            typeList.add(TYPE_INFO);
            typeList.add(TYPE_BANNER);
            typeList.add(TYPE_INSERT);
        }
        return typeList;
    }

    public static AdSlotType getInfoType(int position) {
        return infoList.get(position);
    }

    public static AdSlotType getBannerType(int position) {
        return bannerList.get(position);
    }

    public static AdSlotType getInsertType(int position) {
        return insertList.get(position);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImgW() {
        return imgW;
    }

    public void setImgW(int imgW) {
        this.imgW = imgW;
    }

    public int getImgH() {
        return imgH;
    }

    public void setImgH(int imgH) {
        this.imgH = imgH;
    }

    public int getIconW() {
        return iconW;
    }

    public int getIconH() {
        return iconH;
    }

    public int getStandardWidth() {
        return standardWidth;
    }

    public static List<AdSlotType> getBannerList() {
        return bannerList;
    }

    public static List<AdSlotType> getOpenList() {
        return openList;
    }

    public static List<AdSlotType> getInfoList() {
        return infoList;
    }

    public static List<AdSlotType> getInsertList() {
        return insertList;
    }
}
