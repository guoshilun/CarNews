package com.jmgsz.lib.adv.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.jmgzs.lib_network.utils.L;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * 获取手机信息工具类<br>
 * 内部已经封装了打印功能,只需要把DEBUG参数改为true即可<br>
 * 如果需要更换tag可以直接更改,默认为KEZHUANG
 */
public class DeviceUtils {


    /**
     * 获取应用程序的IMEI号
     */
    public static String getDeviceId(Context context) {
        if (context != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telecomManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telecomManager.getDeviceId();//可能为15个0
            if (imei == null) imei = "";
            String androidid = android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            if (androidid == null) androidid = "";
            String simNum = telecomManager.getSimSerialNumber();
            if (simNum == null) simNum = "";
            UUID uuid = new UUID(androidid.hashCode(), ((long) imei.hashCode() << 32 | simNum.hashCode()));
            L.i("IMEI标识(DeviceId)：" + imei + ",androidid:" + androidid + ",simNum:" + simNum +
                    " , uuid:" + uuid.toString());
            return uuid.toString();
        } else {
            return "";
        }
    }

    /**
     * 获取设备的系统版本号
     */
    public static int getDeviceSDK() {
        int sdk = Build.VERSION.SDK_INT;
        L.i("设备版本：" + sdk);
        return sdk;
    }

    /**
     * 获取设备的型号
     */
    public static String getDeviceName() {
        String model = Build.MODEL;
        L.i("设备型号：" + model);
        return model;
    }

    /**
     * 获取品牌
     */
    public static String getBrand() {
        String model = Build.BRAND;
        L.i("设备品牌：" + model);
        return model;
    }


    /**
     * 获取系统版本
     */
    public static String getOS() {
        String model = Build.VERSION.RELEASE;
        L.i("系统版本：" + model);
        return model;
    }

    /**
     * 获取系统版本名
     *
     * @return
     */
    public static String getOSName() {
        String osName = Build.DISPLAY;
        L.i("系统版本名：" + osName);
        return osName;
    }

    /**
     * 获取手机名称
     */
    public static String getName() {
//		String model = Build.DEVICE;
//		L.i("手机名称：" + model);
//		return model;
        String phoneInfo = "Product: " + Build.PRODUCT;
        phoneInfo += ", CPU_ABI: " + Build.CPU_ABI;
        phoneInfo += ", TAGS: " + Build.TAGS;
        phoneInfo += ", VERSION_CODES.BASE: " + Build.VERSION_CODES.BASE;
        phoneInfo += ", MODEL: " + Build.MODEL;
        phoneInfo += ", SDK: " + Build.VERSION.SDK;
        phoneInfo += ", VERSION.RELEASE: " + Build.VERSION.RELEASE;
        phoneInfo += ", DEVICE: " + Build.DEVICE;
        phoneInfo += ", DISPLAY: " + Build.DISPLAY;
        phoneInfo += ", BRAND: " + Build.BRAND;
        phoneInfo += ", BOARD: " + Build.BOARD;
        phoneInfo += ", FINGERPRINT: " + Build.FINGERPRINT;
        phoneInfo += ", ID: " + Build.ID;
        phoneInfo += ", MANUFACTURER: " + Build.MANUFACTURER;

        L.e("手机基本信息：" + phoneInfo);
        return phoneInfo;
    }

    /**
     * 获取设备号，(友盟测试时 ，后台需要配置的测试设备)
     *
     * @param context
     * @return JSON string
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAddress(Context context) {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)
                context
                        .getSystemService(Context.WIFI_SERVICE);
        String mac = wifi.getConnectionInfo().getMacAddress();
        if (TextUtils.isEmpty(mac)) {
            mac = "";
        }
        return mac;
    }

    /**
     * 获取AndroidID
     *
     * @return
     */
    public static String getAndroidID(Context context) {
        String androidid = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        if (TextUtils.isEmpty(androidid))
            androidid = "";
        return androidid;
    }

    /**
     * 获取设备IMEI
     *
     * @return
     */
    public static String getIMEI(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telecomManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telecomManager.getDeviceId();//可能为15个0
            if (imei == null || imei.equals("000000000000000"))
                imei = "0";
            return imei;
        } else {
            return "0";
        }
    }


    /**
     * 获取手机号
     *
     * @return
     */
    public static String getPhoneNum(Context context) {
        TelephonyManager telecomManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String phone = telecomManager.getLine1Number();
        if (TextUtils.isEmpty(phone)) phone = "";
        return phone;
    }

    /**
     * 获取设备当前联网 IP地址
     *
     * @return
     */
    public static String getDeviceCurrentIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }


    /**
     * 获取屏幕方向
     *
     * @return
     */
    public static boolean isScreenPortrait(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            L.i("info", "landscape");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否平板设备
     *
     * @return true:平板,false:手机
     */
    public static boolean getDeviceType(Context context) {
        boolean isTablet = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;

        if (isTablet)
            return true;
        else return false;
    }

    /**
     * 系统自带定位
     *
     * @return [lat lng]
     */
    public static double[] getLocation(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager
                        .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            }
            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();
                double lng = lastKnownLocation.getLongitude();
                L.i("lat , lng ====" + lat + "," + lng);
                return bd_encrypt(lat, lng);
                // return new double[] { lat, lng };
            }
        }
        return new double[]{0, 0};
    }


    public static String getLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 火星转百度坐标
     */
    public static double[] bd_encrypt(double gg_lat, double gg_lon) {
        double x_pi = Math.PI * 3000.0 / 180.0;
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        // bd_lon = z * Math.cos(theta) + 0.0065;
        // bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{z * Math.sin(theta) + 0.006,
                z * Math.cos(theta) + 0.0065};
    }

    /**
     * CMCC 中国移动, CUCC 中国联通， CTCC 中国电信
     */
    public enum Carrier {
        CMCC, CUCC, CTCC, UNKNOWN;
    }

    public static long getCarrierValue(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            return Long.parseLong(telManager.getSimOperator());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Carrier getCarrier(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String operator = telManager.getSimOperator();
        Carrier result = Carrier.UNKNOWN;
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                //中国移动
                result = Carrier.CMCC;
            } else if (operator.equals("46001")) {
                //中国联通
                result = Carrier.CUCC;
            } else if (operator.equals("46003")) {
                //中国电信
                result = Carrier.CTCC;
            }
        }
        return result;
    }
}
