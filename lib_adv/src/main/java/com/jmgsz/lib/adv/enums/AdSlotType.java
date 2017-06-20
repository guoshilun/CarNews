package com.jmgsz.lib.adv.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 应用对应的广告位类型
 * Created by Wxl on 2017/6/20.
 */

public enum AdSlotType {
    OPEN_640_960(0, 640, 960, 2033), OPEN_640_960_W(0, 640, 960, 2071), OPEN_1182_1620(0, 1182, 1620, 2030), OPEN_720_1066(0, 720, 1066, 2031), OPEN_480_658(0, 480, 658, 2032), OPEN_640_1136(0, 640, 1136, 2024), OPEN_640_1136_W(0, 640, 1136, 2072),
    INFO_720_405_W(1, 720, 405, 2039), INFO_800_120_W(1, 800, 120, 2029), INFO_600_300_W(1, 600, 300, 2021),
    BANNER_640_100(2, 640, 100, 2044), BANNER_800_120(2, 800, 120, 2017), BANNER_640_200(2, 640, 200, 2018), BANNER_440_160(2, 440, 160, 2019), BANNER_240_180_W(2, 240, 180, 2003), BANNER_1000_500_W(2, 1000, 500, 2061),
    INSERT_600_500(3, 600, 500, 2020), INSERT_600_600(3, 600, 600, 2021), INSERT_640_960(3, 640, 960, 2022), INSERT_480_800(3, 480, 800, 2023), INSERT_600_500_W(3, 600, 500, 2004);

    private static List<AdSlotType> openList;
    private static List<AdSlotType> infoList;
    private static List<AdSlotType> bannerList;
    private static List<AdSlotType> insertList;

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
    private int width;
    private int height;
    private int templateId;

    AdSlotType(int type, int width, int height, int templateId) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.templateId = templateId;
    }

    public static AdSlotType getWidthHeightByTemplateId(int templateId) {
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
}
