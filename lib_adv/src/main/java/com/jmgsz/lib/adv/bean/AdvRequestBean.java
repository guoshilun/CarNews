package com.jmgsz.lib.adv.bean;

import android.content.Context;

import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgsz.lib.adv.utils.DeviceUtils;
import com.jmgzs.lib_network.utils.NetworkUtils;

import java.util.Arrays;
import java.util.List;

import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_MOBILE_2G;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_MOBILE_3G;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_MOBILE_4G;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_NONE;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_WIFI;

/**
 * 广告请求对象
 * Created by Wxl on 2017/6/20.
 */

public class AdvRequestBean {
    /**
     * id : ebb7fbcb-01da-4255-8c87-98eedbcd2909
     * user_agent : Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30
     * app_site_info : {"appsite_id":"51e71da6-5b46-4d9f-b94f-9ec6a","categories":[0],"app_bundle_name":"dongfangtoutiao_test","app_name":""}
     * net_type : 2
     * ad_slot_info : [{"sid":0,"height":220,"screen_position":1,"loc_id":"bf8a85e6-849e-11e6-8c73-a4dcbef43d46","width":156,"ad_num":1,"html_material":false}]
     * id_info : {"mac":"d8:55:a3:ce:e4:40","idfa":"5b7e9e4f42a6635f"}
     * device_info : {"orientation":2,"model":"MX5","brand":"MEIXU","screen_width":1080,"type":2,"screen_height":1920}
     * user_ip : 58.30.22.0
     * template_id : [2044,2001]
     * channel_id : 1001
     */

    private String id;
    private String user_agent;
    private AppSiteInfoBean app_site_info;
    private int net_type;
    private IdInfoBean id_info;
    private DeviceInfoBean device_info;
    private String user_ip;
    private int channel_id;
    private List<AdSlotInfoBean> ad_slot_info;
    private List<Integer> template_id;

    public AdvRequestBean() {
    }

    public AdvRequestBean(Context context, AdSlotType type, int channelId) {
        id = "ebb7fbcb-01da-4255-8c87-98eedbcd2909";
        net_type = mapNetworkState(context);
        user_agent = "Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
        this.app_site_info = new AppSiteInfoBean();
        app_site_info.setAppsite_id("51e71da6-5b46-4d9f-b94f-9ec6a");
        app_site_info.setApp_bundle_name(context.getPackageName());
        app_site_info.setApp_name("");
        app_site_info.setCategories(Arrays.asList(0));

        IdInfoBean deviceId = new IdInfoBean();
        deviceId.setMac("d8:55:a3:ce:e4:40");
        deviceId.setIdfa("5b7e9e4f42a6635f");

        DeviceInfoBean device = new DeviceInfoBean();
        device.setBrand(DeviceUtils.getBrand());
        device.setModel(DeviceUtils.getBrand());
        device.setOrientation(2);
        device.setModel(DeviceUtils.getDeviceName());
        device.setScreen_width(DensityUtils.getScreenWidthPixels(context));
        device.setScreen_height(DensityUtils.getScreenHeightPixels(context));

        user_ip = DeviceUtils.getDeviceCurrentIP();

        channel_id = channelId;
        template_id = Arrays.asList(type.getTemplateId());
        ad_slot_info = Arrays.asList(setAdSlotInfo(type.getWidth(), type.getHeight()));
    }

    private static AdSlotInfoBean setAdSlotInfo(int width, int height){
        AdSlotInfoBean adSlot = new AdSlotInfoBean();
        adSlot.setAd_num(1);
        adSlot.setHeight(height);
        adSlot.setWidth(width);
        adSlot.setSid(0);
        adSlot.setScreen_position(1);
        adSlot.setHtml_material(false);
        adSlot.setLoc_id("bf8a85e6-849e-11e6-8c73-a4dcbef43d46");
        return adSlot;
    }

    /**
     * NETWORK_UNKNOWN = 1;
     * NETWORK_WIFI  = 2;
     * NETWORK_G2    = 3;                      // 2nd generation mobile network
     * NETWORK_G3    = 4;                      // 3rd generation
     * NETWORK_G4    = 5;                      // 4th generation
     * NETWORK_G5    = 6;
     **/
    private static int mapNetworkState(Context context) {
        int result;
        switch (NetworkUtils.getNetworkState(context)){
            case NETWORN_NONE:
                result = 1;
                break;
            case NETWORN_WIFI:
                result = 2;
                break;
            case NETWORN_MOBILE_2G:
                result = 3;
                break;
            case NETWORN_MOBILE_3G:
                result = 4;
                break;
            case NETWORN_MOBILE_4G:
                result = 5;
                break;
            default:
                result = 1;
                break;
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public AppSiteInfoBean getApp_site_info() {
        return app_site_info;
    }

    public void setApp_site_info(AppSiteInfoBean app_site_info) {
        this.app_site_info = app_site_info;
    }

    public int getNet_type() {
        return net_type;
    }

    public void setNet_type(int net_type) {
        this.net_type = net_type;
    }

    public IdInfoBean getId_info() {
        return id_info;
    }

    public void setId_info(IdInfoBean id_info) {
        this.id_info = id_info;
    }

    public DeviceInfoBean getDevice_info() {
        return device_info;
    }

    public void setDevice_info(DeviceInfoBean device_info) {
        this.device_info = device_info;
    }

    public String getUser_ip() {
        return user_ip;
    }

    public void setUser_ip(String user_ip) {
        this.user_ip = user_ip;
    }

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public List<AdSlotInfoBean> getAd_slot_info() {
        return ad_slot_info;
    }

    public void setAd_slot_info(List<AdSlotInfoBean> ad_slot_info) {
        this.ad_slot_info = ad_slot_info;
    }

    public List<Integer> getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(List<Integer> template_id) {
        this.template_id = template_id;
    }

    public static class AppSiteInfoBean {
        /**
         * appsite_id : 51e71da6-5b46-4d9f-b94f-9ec6a
         * categories : [0]
         * app_bundle_name : dongfangtoutiao_test
         * app_name :
         */

        private String appsite_id;
        private String app_bundle_name;
        private String app_name;
        private List<Integer> categories;

        public String getAppsite_id() {
            return appsite_id;
        }

        public void setAppsite_id(String appsite_id) {
            this.appsite_id = appsite_id;
        }

        public String getApp_bundle_name() {
            return app_bundle_name;
        }

        public void setApp_bundle_name(String app_bundle_name) {
            this.app_bundle_name = app_bundle_name;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public List<Integer> getCategories() {
            return categories;
        }

        public void setCategories(List<Integer> categories) {
            this.categories = categories;
        }
    }

    public static class IdInfoBean {
        /**
         * mac : d8:55:a3:ce:e4:40
         * idfa : 5b7e9e4f42a6635f
         */

        private String mac;
        private String idfa;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getIdfa() {
            return idfa;
        }

        public void setIdfa(String idfa) {
            this.idfa = idfa;
        }
    }

    public static class DeviceInfoBean {
        /**
         * orientation : 2
         * model : MX5
         * brand : MEIXU
         * screen_width : 1080
         * type : 2
         * screen_height : 1920
         */

        private int orientation;
        private String model;
        private String brand;
        private int screen_width;
        private int type;
        private int screen_height;

        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public int getScreen_width() {
            return screen_width;
        }

        public void setScreen_width(int screen_width) {
            this.screen_width = screen_width;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getScreen_height() {
            return screen_height;
        }

        public void setScreen_height(int screen_height) {
            this.screen_height = screen_height;
        }
    }

    public static class AdSlotInfoBean {
        /**
         * sid : 0
         * height : 220
         * screen_position : 1
         * loc_id : bf8a85e6-849e-11e6-8c73-a4dcbef43d46
         * width : 156
         * ad_num : 1
         * html_material : false
         */

        private int sid;
        private int height;
        private int screen_position;
        private String loc_id;
        private int width;
        private int ad_num;
        private boolean html_material;

        public int getSid() {
            return sid;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getScreen_position() {
            return screen_position;
        }

        public void setScreen_position(int screen_position) {
            this.screen_position = screen_position;
        }

        public String getLoc_id() {
            return loc_id;
        }

        public void setLoc_id(String loc_id) {
            this.loc_id = loc_id;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getAd_num() {
            return ad_num;
        }

        public void setAd_num(int ad_num) {
            this.ad_num = ad_num;
        }

        public boolean isHtml_material() {
            return html_material;
        }

        public void setHtml_material(boolean html_material) {
            this.html_material = html_material;
        }
    }
}
