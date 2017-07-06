package com.jmgsz.lib.adv;

import android.app.Activity;

import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.lib_network.utils.L;

import java.util.ArrayList;
import java.util.List;

import static com.jmgsz.lib.adv.utils.DensityUtils.SCREEN_WIDTH_PIXELS;

/**
 * 广告缓存列表工具类
 * Created by Wxl on 2017/6/30.
 */

public class AdvTempList {
    private static List<AdvTempBean> list_740_405 = new ArrayList<>();
    private static List<AdvTempBean> list_800_120 = new ArrayList<>();
    private static List<AdvTempBean> list_600_300 = new ArrayList<>();

    public static void requestAdvTempList(final Activity context, int count, final AdSlotType type) {
        AdvRequestBean req = AdvRequestUtil.getAdvRequest(context, type);
        for (int i = 0; i < count; i++) {
            AdvRequestUtil.requestAdv(context, 0, req, new IAdvRequestCallback() {
                @Override
                public void onGetAdvSuccess(String html, int width, int height) {
                    L.e("广告请求成功");
                    L.e("adv Html:" + html);
                    if (AdSlotType.INFO_720_405_W == type) {
                        list_740_405.add(new AdvTempBean(html, width, height));
                    } else if (AdSlotType.INFO_600_300_W == type) {
                        list_600_300.add(new AdvTempBean(html, width, height));
                    } else if (AdSlotType.INFO_800_120_W == type) {
                        list_800_120.add(new AdvTempBean(html, width, height));
                    }
                }

                @Override
                public void onGetAdvFailure() {
                    L.e("广告请求失败");
                    String res = "{\"ad_info\":[{\"ad_material\":{\"click_url\":\"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\":[\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\":[\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\":2,\"adid\":4398,\"landing_page\":\"http://lai8sy.com/\"}],\"id\":\"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                    String html = AdvRequestUtil.getHtmlByResponse(context, 0, type, res);
                    int[] real = getScaleSize(type.getWidth(), type.getHeight());
                    if (AdSlotType.INFO_720_405_W == type) {
                        list_740_405.add(new AdvTempBean(html, real[0], real[1]));
                    } else if (AdSlotType.INFO_600_300_W == type) {
                        list_600_300.add(new AdvTempBean(html, real[0], real[1]));
                    } else if (AdSlotType.INFO_800_120_W == type) {
                        list_800_120.add(new AdvTempBean(html, real[0], real[1]));
                    }
                }
            });
        }
    }

    private static int[] getScaleSize(int w, int h) {
        int[] realSize = new int[2];
        int sw = DensityUtils.SCREEN_WIDTH_PIXELS - DensityUtils.dip2px(5) * 2;
        float scale = sw * 1.0f / w;
        realSize[0] = sw;
        realSize[1] = Float.valueOf(h * scale).intValue();
        return realSize;
    }

    public static List<AdvTempBean> getList_740_405(int count) {
        if (list_740_405.size() <= count) {
            List<AdvTempBean> data = list_740_405;
            list_740_405 = new ArrayList<>();
            return data;
        } else {
            return list_740_405.subList(0, count);
        }
    }

    public static List<AdvTempBean> getList_800_120(int count) {
        if (list_800_120.size() <= count) {
            List<AdvTempBean> data = list_800_120;
            list_800_120 = new ArrayList<>();
            return data;
        } else {
            return list_800_120.subList(0, count);
        }
    }

    public static List<AdvTempBean> getList_600_300(int count) {
        if (list_600_300.size() <= count) {
            List<AdvTempBean> data = list_600_300;
            list_600_300 = new ArrayList<>();
            return data;
        } else {
            return list_600_300.subList(0, count);
        }
    }


    public static List<AdvTempBean> getList_740_405() {
        return getList_740_405(1);
    }

    public static List<AdvTempBean> getList_800_120() {
        return getList_800_120(1);
    }

    public static List<AdvTempBean> getList_600_300() {
        return getList_600_300(1);
    }

    public static class AdvTempBean {
        private String html;
        private int width;
        private int height;

        public AdvTempBean(String html, int width, int height) {
            this.html = html;
            this.width = width;
            this.height = height;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
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
    }
}
