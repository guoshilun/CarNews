package com.jmgzs.carnews.util;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jmgzs.carnews.base.App;
import com.jmgzs.lib_network.utils.L;

import java.security.MessageDigest;

/**
 * Created by mac on 17/6/29.
 * Description:
 */

public class AppUtils {

    private final static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private AppUtils() {

    }

    public static String getMD5BundleId() {
        String md5 = toMD5(App.getInstance().getPackageName());
        L.e("md5="+md5);
        return md5;
    }

    public static int getVersionNum() {
        try {
            PackageManager pm = App.getInstance().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(App.getInstance().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getVersionName(){
        try {
            PackageManager pm = App.getInstance().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(App.getInstance().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";

    }

    public static String getAppName(){
        try {
            PackageManager pm = App.getInstance().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(App.getInstance().getPackageName(), 0);
            int labelId = pi.applicationInfo.labelRes;
            return App.getInstance().getResources().getString(labelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }

    private static String toMD5(String s) {
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
