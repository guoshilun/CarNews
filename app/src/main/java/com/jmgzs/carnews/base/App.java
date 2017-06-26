package com.jmgzs.carnews.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.jmgzs.carnews.push.PushUtil;
import com.jmgzs.carnews.util.Const;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.util.ShareUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.carnews.util.SPBase;

import java.util.List;

import static android.R.attr.key;
import static android.R.attr.level;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class App extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static boolean isMobile = false;
    public static boolean isRecptPush = true;
    public static String headPath;
    private static App instance;
    private Handler handler = new Handler();

    public static App getInstance() {
        return instance;
    }

    public void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }

    public void runOnUiThread(int delay, Runnable runnable){
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPush();
        initShare();
        instance = this;
        if (shouldInit())
            init();
    }

    private void initPush(){
        PushUtil.getPush().appInit(this);
    }

    private void initShare(){
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

    private void initSP(SharedPreferences sharedPreferences){
        isMobile = sharedPreferences.getBoolean(Const.SPKey.WIFI,false);
        isRecptPush = sharedPreferences.getBoolean(Const.SPKey.PUSH,true);
        headPath = sharedPreferences.getString(Const.SPKey.HEAD_PATH,null);
        L.i("app  sp --:"+key +"-"+ isRecptPush+isMobile+headPath);
    }

}
