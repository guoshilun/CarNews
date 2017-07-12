package com.jmgzs.carnews.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by mac on 17/7/12.
 * Description:umeng 统计相关
 */

public final class UmengUtil {

    public static final String U_SPLASH = "ui_splash";//,启动页,0
    public static final String U_MAIN = "ui_main";//,首页,0
    public static final String U_SETTING = "ui_setting";//,个人设置,0
    public static final String U_NEWS_DETAIL = "ui_news_detail";//,新闻详情,0
    public static final String U_STORELIST = "ui_storelist";//,新闻收藏列表,0
    public static final String U_CHANNEL = "key_channel_click";//,channel,1
    public static final String U_SETTING_STORE = "key_storelist_click";//,我的收藏,0
    public static final String U_NOTIFY = "key_notification_click";//,通知栏,0
    public static final String U_SETTING_HEAD = "key_header_click";//,头像,0
    public static final String U_SHARE = "key_share_click";//,分享,0
    public static final String U_STORE_BTN = "key_store_btn_click";//,收藏,0
    public static final String U_CHANNEL_NAME = "channel_name";
    public static final String U_CHANNEL_COUNT = "channel_count";


    public static void event(Context  ct , String key){
        MobclickAgent.onEvent(ct , key);
    }


    public static void event(Context ct , String key , Map<String,String> map){
        MobclickAgent.onEvent(ct,key,map);
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
