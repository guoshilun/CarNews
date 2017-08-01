package com.jmgzs.carnews.base;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.jmgsz.lib.adv.AdvUtil;
import com.jmgzs.carnews.push.PushUtil;
import com.jmgzs.carnews.util.Const;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.util.ShareUtils;
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.carnews.util.SPBase;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;

import static android.R.attr.key;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class App extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static boolean isMobile = false;
    public static boolean isRecptPush = true;
    public static String headPath;
    private static App instance;
    private Handler handler;

    public static App getInstance() {
        return instance;
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void runOnUiThread(int delay, Runnable runnable) {
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        initPush();
        initShare();
        instance = this;
        if (shouldInit())
            init();
        AdvUtil.getInstance(this).init(FileUtils.getCachePath(this)+ File.separator + "adv");
        AdvUtil.setAdvOpen(SPBase.getBoolean(Const.SPKey.OPEN_ADV, true));
    }

    private void initPush() {
        PushUtil.getPush().appInit(this);
    }

    private void initShare() {
        ShareUtils.init(this);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void init() {
        SPBase.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        DensityUtils.init(this);
        initSP(SPBase.getSharedPreferences());
        //umeng统计
        MobclickAgent.setDebugMode(false);
//        L.i(UmengUtil.getDeviceInfo(this));
        initLocation();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SPBase.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initSP(sharedPreferences);
    }

    private void initSP(SharedPreferences sharedPreferences) {
        isMobile = sharedPreferences.getBoolean(Const.SPKey.WIFI, false);
        isRecptPush = sharedPreferences.getBoolean(Const.SPKey.PUSH, true);
        headPath = sharedPreferences.getString(Const.SPKey.HEAD_PATH, null);
        L.i("app  sp --:" + key + "-" + isRecptPush + isMobile + headPath);
    }

    private void initLocation() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        // 获得最好的定位效果
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(false);
        // 使用省电模式
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        final String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 5 * 3600 * 1000L,
                    1000f, new LocationListener() {

                        @Override
                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }

                        @Override
                        public void onLocationChanged(Location location) {
                            L.i("system  location change:" + location.getLatitude() + "," + location.getLongitude());
                        }
                    });
        }
    }

}
