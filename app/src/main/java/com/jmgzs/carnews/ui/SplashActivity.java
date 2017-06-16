package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class SplashActivity extends BaseActivity {


    private ImageView imageView;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        overridePendingTransition(0,0);
        imageView = getView(R.id.welcome_view);

        Handler h = new Handler();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goMain();
                    }
                });
            }
        }, 2000);


    }

    private void goMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();

    }
}
