package com.jmgzs.carnews.ui;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib_network.utils.FileUtils;

import java.io.File;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class SplashActivity extends com.jmgsz.lib.adv.ui.SplashActivity {

    @Override
    protected int getLogoResId() {
        return R.mipmap.logo;
    }

    @Override
    protected String getAppInfo() {
        return this.getResources().getString(R.string.splash_text);
    }

    @Override
    protected String getActivityName() {
        return MainActivity.class.getName();
    }

    @Override
    protected String getTempDir() {
        return FileUtils.getCachePath(this) + File.separator + "info";
    }
}
