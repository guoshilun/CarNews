package com.jmgzs.carnews.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.DensityUtils;
import com.jmgzs.carnews.util.L;
import com.jmgzs.carnews.util.SPBase;

import java.util.List;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class App extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static boolean isOpenWifi = false;
    public static boolean isRecptPush = true;
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (shouldInit())
            init();
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
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        SPBase.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        isOpenWifi = sharedPreferences.getBoolean(Const.SPKey.WIFI,false);
        isRecptPush = sharedPreferences.getBoolean(Const.SPKey.PUSH,true);
        L.e("app  sp --:"+key +"-"+ isRecptPush+isOpenWifi);
    }

}
