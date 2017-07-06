package com.jmgsz.lib.adv;

import android.app.Activity;

import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.lib_network.utils.L;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.jmgsz.lib.adv.utils.DensityUtils.SCREEN_WIDTH_PIXELS;

/**
 * 广告缓存列表工具类
 * Created by Wxl on 2017/6/30.
 */

public class AdvTempList {
    private static List<AdvTempBean> list_720_405 = new ArrayList<AdvTempBean>();
    private static List<AdvTempBean> list_800_120 = new ArrayList<AdvTempBean>();
    private static List<AdvTempBean> list_600_300 = new ArrayList<AdvTempBean>();

    public static void requestAdvTempList(final Activity context, int width, int count, final AdSlotType slotType) {
//        AdvRequestBean req = AdvRequestUtil.getAdvRequest(context, slotType);
        for (int i = 0; i < count; i++) {
            String response;
            if (slotType == AdSlotType.INFO_720_405_W){
                response = "{\"id\":ebb7fbcb-01da-4255-8c87-98eedbcd2909,\"ad_info\":[{\"ad_material\":{\"app_download_url\":\"http://mj-public.weimob.com/wd/mengdian_360_4.5.3_signed_Aligned.apk\",\"app_name\":\"\\u840c\\u5e97\",\"app_pkg_name\":\"com.hs.yjseller\",\"brand\":\"\\u5fae\\u76df\\u840c\\u5e97\",\"click_url\":\"http://c.mjmobi.com/cli?info=ChhDTEtaNFpmUEt4Q3dvTUZRR0lDYS1qMD0QACC4DijbiJr5prH4mTMwspnhl88rOg4I8yEQyt0EGMTiAyD7I0C8BVgxYkRodHRwOi8vbWotcHVibGljLndlaW1vYi5jb20vd2QvbWVuZ2RpYW5fMzYwXzQuNS4zX3NpZ25lZF9BbGlnbmVkLmFwa2gBcACAAZcPiAHo_gKQAQGYAQawAQY=\",\"desc\":\"\\u708e\\u708e\\u9177\\u6691\\u4e2d\\u7684\\u590f\\u5b63\\u6e05\\u51c9\\u6c34\\u679c\\uff0c\\u4fbf\\u5b9c\\u53c8\\u65b0\\u9c9c\",\"icon\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/36cee647-e6ec-49fa-8d42-e12915d2d28f.png\",\"images\":[\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/ad3d10bb-f5e7-4abe-88e2-e3d1103df186.jpg\"],\"show_urls\":[\"http://s.mjmobi.com/imp?info=ChhDTEtaNFpmUEt4Q3dvTUZRR0lDYS1qMD0Q24ia-aax-JkzGg4I8yEQyt0EGMTiAyD7IyCymeGXzysovAU4lw9AuA6YAQaoAQawAQG4AQDAAej-AsgBAQ==&seqs=0\"],\"title\":\"\\u590f\\u5b63\\u6c34\\u679c\\u9c9c\\u6ecb\\u5473\\uff0c\\u9650\\u65f6\\u6298\\u4e0a\\u6298\"},\"ad_type\":1,\"adid\":4603,\"landing_page\":\"http://mj-public.weimob.com/wd/mengdian_360_4.5.3_signed_Aligned.apk\"}]}";
            }else if (slotType == AdSlotType.INFO_600_300_W){
                L.e("600_300信息流暂无数据");
                return;
//                response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
            }else if (slotType == AdSlotType.INFO_800_120_W){
                L.e("800_230信息流暂无数据");
                return;
//                response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
            }else{
                return;
            }
            int height = slotType.getHeight() * width / slotType.getWidth();
            String html = AdvRequestUtil.getHtmlByResponse(context, DensityUtils.getScreenWidthPixels(context) - DensityUtils.dip2px(context, 8 * 2), slotType.getImgW(), slotType.getImgH(), slotType.getIconW(), slotType.getIconH(), slotType, response);
            if (AdSlotType.INFO_720_405_W == slotType) {
                list_720_405.add(new AdvTempBean(html, width, height));
            } else if (AdSlotType.INFO_600_300_W == slotType) {
                list_600_300.add(new AdvTempBean(html, width, height));
            } else if (AdSlotType.INFO_800_120_W == slotType) {
                list_800_120.add(new AdvTempBean(html, width, height));
            }
            /*AdvRequestUtil.requestAdv(context, 0, req, new IAdvRequestCallback() {
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
                }
            });*/
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

    public static List<AdvTempBean> getList_720_405(int count) {
        if (list_720_405.size() <= count) {
            List<AdvTempBean> data = list_720_405;
            list_720_405 = new ArrayList<>();
            return data;
        }else{
            List<AdvTempBean> data = new ArrayList<>(list_720_405.subList(0, count));
            list_720_405.removeAll(data);
            return data;
        }
    }

    public static List<AdvTempBean> getList_800_120(int count) {
        if (list_800_120.size() <= count) {
            List<AdvTempBean> data = list_800_120;
            list_800_120 = new ArrayList<>();
            return data;
        }else{
            List<AdvTempBean> data = new ArrayList<>(list_800_120.subList(0, count));
            list_800_120.removeAll(data);
            return data;
        }
    }

    public static List<AdvTempBean> getList_600_300(int count) {
        if (list_600_300.size() <= count) {
            List<AdvTempBean> data = list_600_300;
            list_600_300 = new ArrayList<>();
            return data;
        }else{
            List<AdvTempBean> data = new ArrayList<>(list_600_300.subList(0, count));
            list_600_300.removeAll(data);
            return data;
        }
    }


    public static List<AdvTempBean> getList_720_405() {
        return getList_720_405(1);
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
