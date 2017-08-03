package com.jmgzs.carnews.ui;

import com.jmgzs.lib.adv.ui.AdvSplashActivity;
import com.jmgzs.carnews.R;
import com.jmgzs.lib_network.utils.FileUtils;

import java.io.File;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class SplashActivity extends AdvSplashActivity {

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
