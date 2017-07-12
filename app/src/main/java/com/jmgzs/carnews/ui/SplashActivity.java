package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvResponseBean;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.SPBase;
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;

import retrofit2.http.Url;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class SplashActivity extends BaseActivity {


    private ImageView imageView;
    private TextView timeTv;
    private Handler h = new Handler();

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        imageView = getView(R.id.welcome_view);
        timeTv = getView(R.id.text_time);
        showAdv();
    }

    @Override
    protected String getUmengKey() {
        return UmengUtil.U_SPLASH;
    }


    @Override
    protected void addPaddingAboveContentView() {

    }

    private void showAdv() {
        AdvRequestUtil.requestOpenAdv(this, new IRequestCallBack<AdvResponseBean.AdInfoBean>() {
            @Override
            public void onSuccess(String url, final AdvResponseBean.AdInfoBean data) {
                if (SPBase.getBoolean(Const.SPKey.OPEN_ADV, false))
                    loadImage(data);
                else delayGoMain();
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                delayGoMain();
            }

            @Override
            public void onCancel(String url) {
                if (url != null && url.startsWith("http"))
                    GlideApp.with(SplashActivity.this).downloadOnly().load(url);
                delayGoMain();
            }
        });
    }

    private void loadImage(final AdvResponseBean.AdInfoBean data) {
        imageView.setVisibility(View.VISIBLE);
        GlideApp.with(SplashActivity.this).asDrawable().centerCrop()
                .load(data.getAd_material().getImages().get(0)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                goMain();
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, final Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                imageView.setImageDrawable(resource);
                countDownText();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(SplashActivity.this, WebViewActivity.class);
                        in.putExtra("url", data.getAd_material().getClick_url());
                        in.putExtra("fromSplash", true);
                        startActivity(in);
                        finish();
                    }
                });
                if (data.getAd_material().getShow_urls() != null &&
                        data.getAd_material().getShow_urls().size() > 0)
                    RequestUtil.requestByGetAsy(SplashActivity.this,
                            data.getAd_material().getShow_urls().get(0), Void.class, null);

                return true;
            }
        }).into(imageView);

    }

    private CountDownTimer countDownTimer;

    private void countDownText() {
        timeTv.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTv.setText("广告 " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                goMain();
            }
        };
        countDownTimer.start();
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain();
                cancelTimer();
            }
        });
    }

    private void delayGoMain() {
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


    @Override
    protected void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
