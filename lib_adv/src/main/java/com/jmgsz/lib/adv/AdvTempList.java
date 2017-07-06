package com.jmgsz.lib.adv;

import android.app.Activity;

import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgzs.lib_network.utils.L;

import java.util.ArrayList;
import java.util.List;

/**广告缓存列表工具类
 * Created by Wxl on 2017/6/30.
 */

public class AdvTempList {
    private static List<AdvTempBean> list_740_405, list_800_120, list_600_300;

    public static void requestAdvTempList(Activity context, int count, final AdSlotType type){
        AdvRequestBean req = AdvRequestUtil.getAdvRequest(context, type);
        for(int i = 0; i < count; i++){
            AdvRequestUtil.requestAdv(context, 0, req, new IAdvRequestCallback() {
                @Override
                public void onGetAdvSuccess(String html, int width, int height) {
                    L.e("广告请求成功");
                    L.e("adv Html:"+html);
                    if (AdSlotType.INFO_720_405_W == type){
                        list_740_405.add(new AdvTempBean(html, width, height));
                    }else if (AdSlotType.INFO_600_300_W == type){
                        list_600_300.add(new AdvTempBean(html, width, height));
                    }else if (AdSlotType.INFO_800_120_W == type){
                        list_800_120.add(new AdvTempBean(html, width, height));
                    }
                }

                @Override
                public void onGetAdvFailure() {
                    L.e("广告请求失败");
                }
            });
        }
    }

    public static List<AdvTempBean> getList_740_405(int count) {
        if (list_740_405.size() <= count){
            List<AdvTempBean> data = list_740_405;
            list_740_405 = new ArrayList<>();
            return data;
        }else{
            List<AdvTempBean> data = list_740_405.subList(0, count);
            list_740_405.removeAll(data);
            return data;
        }
    }

    public static List<AdvTempBean> getList_800_120(int count) {
        if (list_800_120.size() <= count){
            List<AdvTempBean> data = list_800_120;
            list_800_120 = new ArrayList<>();
            return data;
        }else{
            List<AdvTempBean> data = list_800_120.subList(0, count);
            list_800_120.removeAll(data);
            return data;
        }
    }

    public static List<AdvTempBean> getList_600_300(int count) {
        if (list_600_300.size() <= count){
            List<AdvTempBean> data = list_600_300;
            list_600_300 = new ArrayList<>();
            return data;
        }else{
            List<AdvTempBean> data = list_600_300.subList(0, count);
            list_600_300.removeAll(data);
            return data;
        }
    }

    public static class AdvTempBean{
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