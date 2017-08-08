package com.jmgzs.lib.adv.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jmgzs.lib.adv.AdvUtil;
import com.jmgzs.lib.adv.R;
import com.jmgzs.lib.adv.bean.AdvResponseBean;
import com.jmgzs.lib.adv.utils.ThreadPool;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;

/**
 * Created by mac on 17/6/15.
 * Description:
 */

public class AdvSplashActivity extends AdvBaseActivity {


    private ImageView imageView;
    private RoundedImageView logoImgView;
    private TextView timeTv;
    private TextView appInfoTv;
    private Handler h = new Handler();
    private int logoResId;
    private String appInfo;
    private String activityName;
    private Bundle params;

    public static final String INTENT_LOGO = "logo";
    public static final String INTENT_APP_INFO = "app_info";
    public static final String INTENT_ACTIVITY_NAME = "activity_name";
    public static final String INTENT_PARAMS = "params";

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_adv_splash;
    }

    @Override
    protected void initView() {
        ThreadPool.setMainHandler(this);
        imageView = (ImageView) findViewById(R.id.welcome_view);
        logoImgView = (RoundedImageView) findViewById(R.id.welcome_bottom_logo);
        timeTv = (TextView) findViewById(R.id.text_time);
        appInfoTv = (TextView) findViewById(R.id.welcome_bottom_logo_tips);
        initViewData();
        showAdv();
    }

    protected void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            logoResId = intent.getIntExtra(INTENT_LOGO, -1);
            appInfo = intent.getStringExtra(INTENT_APP_INFO);
            activityName = intent.getStringExtra(INTENT_ACTIVITY_NAME);
            params = intent.getBundleExtra(INTENT_PARAMS);
        }
        if (logoResId < 0) {
            logoResId = getLogoResId();
        }
        if (TextUtils.isEmpty(appInfo)) {
            appInfo = getAppInfo();
        }
        if (TextUtils.isEmpty(activityName)) {
            activityName = getActivityName();
        }
        if (params == null){
            params = getParams();
        }
    }

    private void initViewData(){
        if (logoResId > 0) {
            logoImgView.setImageResource(logoResId);
        }
        appInfoTv.setText(appInfo);
    }

    @Override
    protected void addPaddingAboveContentView() {//去除顶部纯色状态栏条
    }

    protected int getLogoResId() {
        return -1;
    }

    protected String getAppInfo() {
        return null;
    }

    protected String getActivityName() {
        return null;
    }

    protected Bundle getParams(){
        return null;
    }

    private void showAdv() {
        AdvUtil.getInstance().showOpenAdv(this, new IRequestCallBack<AdvResponseBean.AdInfoBean>() {
            @Override
            public void onSuccess(String url, final AdvResponseBean.AdInfoBean data) {
                if (AdvUtil.isOpenAdv())
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
                    Glide.with(AdvSplashActivity.this).downloadOnly().load(url);
                delayGoMain();
            }
        });
    }

    private void loadImage(final AdvResponseBean.AdInfoBean data) {
        imageView.setVisibility(View.VISIBLE);
        Glide.with(AdvSplashActivity.this).asDrawable()
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
                        Intent in = new Intent(AdvSplashActivity.this, AdvWebViewActivity.class);
                        in.putExtra(AdvWebViewActivity.INTENT_URL, data.getAd_material().getClick_url());
                        in.putExtra(AdvWebViewActivity.INTENT_ACTIVITY, activityName);
                        in.putExtra(INTENT_PARAMS, params);
                        startActivity(in);
                        finish();
                    }
                });
                if (data.getAd_material().getShow_urls() != null &&
                        data.getAd_material().getShow_urls().size() > 0)
                    RequestUtil.requestByGetAsy(AdvSplashActivity.this,
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
        try {
            Intent intent = new Intent(AdvSplashActivity.this, Class.forName(activityName));
            intent.putExtra(INTENT_PARAMS, params);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
