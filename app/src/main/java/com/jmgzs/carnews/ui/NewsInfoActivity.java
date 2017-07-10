package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.NewsInfoBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.ui.view.ScaleClickUtils;
import com.jmgzs.carnews.ui.view.ScrollControlFrameLayout;
import com.jmgzs.carnews.ui.view.ScrollableWebView;
import com.jmgzs.carnews.ui.view.ShareBoardView;
import com.jmgzs.carnews.ui.view.TitleBarScrollController;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.LoaderUtil;
import com.jmgzs.carnews.util.ResUtils;
import com.jmgzs.carnews.util.SPBase;
import com.jmgzs.carnews.util.ShareUtils;
import com.jmgzs.carnews.util.T;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.NetworkErrorCode;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib_network.utils.NetworkUtils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻详情界面
 * Created by Wxl on 2017/6/12.
 */

public class NewsInfoActivity extends BaseActivity {


    private ScrollControlFrameLayout scrollControlView;
    private ScrollableWebView wv;
    private View content;
    private View top, statusBar;
    private View bottomBar;
    private View contentCover;
    private ImageView btnShare, btnShareTop, btnBack, imgFav;
    private ToggleButton tgbtnFav;
    private TextView tvTitle;
    private JsBridge js;
    private Animation animShareOpen, animShareClose;

    private UMShareListener mShareListener;
    private ShareUtils shareUtils;

    private int newsId;//新闻id
    private String downloadUrl;
    public static final String INTENT_AID = "aid";
    public static final String INTENT_IMAGES = "images";
    public static final String INTENT_CHANNEL = "channel";
    private boolean hasStored = false;
    private NewsDataBean info;
    private String channel;//新闻类型
    private ArrayList<String> images;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_news_info;
    }


    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent == null || 0 > ((newsId = intent.getIntExtra(INTENT_AID, -1)))) {
            Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
            return;
        }
        images = intent.getStringArrayListExtra(INTENT_IMAGES);
        channel = intent.getStringExtra(INTENT_CHANNEL);
//        L.e(images.toString());
        top = findViewById(R.id.newsInfo_top_bar);
        statusBar = findViewById(R.id.newInfo_status_bar);
        bottomBar = findViewById(R.id.newsInfo_bottom_bar);
        contentCover = findViewById(R.id.newsInfo_grey_cover);

        initButtons();
        initTitle();
        initWebView();
        initScroll();
        initShare();

        requestInfo(newsId);

//        wv.loadDataWithBaseURL("file:///android_asset/", htmlTemplate, "text/html", "utf-8", null);
    }
    private void initAnim(){
        animShareOpen = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_show);
        animShareClose = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_hide);
        animShareClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                App.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contentCover.setVisibility(View.GONE);
                        ((ViewGroup)contentCover.getParent().getParent()).invalidate();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void initButtons() {
        btnShare = (ImageView) findViewById(R.id.bottomBar_img_share);
        btnShareTop = (ImageView) findViewById(R.id.titleInfo_img_more);
        btnBack = (ImageView) findViewById(R.id.titleInfo_img_back);

        initOneBtn(btnBack, R.mipmap.arrow_left);
        btnShareTop.setImageResource(R.mipmap.point);
        initOneBtn(btnShareTop, R.mipmap.point);
        btnShare.setImageResource(R.mipmap.share);
        initOneBtn(btnShare, R.mipmap.share);

        initFavBtn();
    }

    private void initFavBtn() {
        imgFav = (ImageView) findViewById(R.id.bottomBar_img_home);
        tgbtnFav = (ToggleButton) findViewById(R.id.bottomBar_tgbtn_home);
        hasStored = LoaderUtil.get().hasStored(this, String.valueOf(newsId));
        tgbtnFav.setChecked(hasStored);
        if (hasStored) {
            imgFav.setImageResource(R.mipmap.fav_2);
        } else {
            imgFav.setImageResource(R.mipmap.fav_1);
        }
        tgbtnFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tgbtnFav.setEnabled(false);
                if (isChecked) {
                    T.toastS("收藏成功");
                    ScaleClickUtils.startScaleSmallAnim(imgFav, new Runnable() {
                        @Override
                        public void run() {
                            App.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ScaleClickUtils.startScaleBigAnim(imgFav, new Runnable() {
                                        @Override
                                        public void run() {
                                            imgFav.setImageResource(R.mipmap.fav_2);
                                            tgbtnFav.setEnabled(true);
                                        }
                                    });
                                }
                            });
                        }
                    });

                } else {
                    T.toastS("已取消收藏");
                    ScaleClickUtils.startScaleSmallAnim(imgFav, new Runnable() {
                        @Override
                        public void run() {
                            App.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ScaleClickUtils.startScaleBigAnim(imgFav, new Runnable() {
                                        @Override
                                        public void run() {
                                            imgFav.setImageResource(R.mipmap.fav_1);
                                            tgbtnFav.setEnabled(true);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void initOneBtn(ImageView btn, int imgRes) {
        btn.setImageResource(imgRes);
        btn.setOnClickListener(this);
        btn.setBackgroundResource(R.drawable.bg_trans_to_black);
    }

    private void initTitle() {
        tvTitle = (TextView) findViewById(R.id.titleInfo_tv_title);
        tvTitle.setText("");
    }

    @Override
    protected void addPaddingAboveContentView() {
    }

    private void initShare() {
        shareUtils = new ShareUtils();
        initAnim();
    }

    private void initWebView() {
        wv = (ScrollableWebView) findViewById(R.id.newsInfo_wv);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(false);
        wv.getSettings().setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wv.getSettings().setAllowFileAccessFromFileURLs(true);
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        wv.setWebChromeClient(new WebChromeClient() {

        });
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("url:" + url);
                if (url.startsWith("file")) {
                    return false;
                }
                if (url.startsWith("http")){
                    Intent intent = new Intent(NewsInfoActivity.this, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.INTENT_URL, url);
                    startActivity(intent);
                }
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                L.e("资源url:"+url);
                if (App.isMobile && !url.contains("mj-img") && !url.startsWith("file://")){
                    int networkState = NetworkUtils.getNetworkState(NewsInfoActivity.this);
                    if (networkState == NetworkUtils.NETWORN_MOBILE_2G || networkState == NetworkUtils.NETWORN_MOBILE_3G || networkState == NetworkUtils.NETWORN_MOBILE_4G){
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ((BitmapDrawable)NewsInfoActivity.this.getResources().getDrawable(R.mipmap.app_default_middle)).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
                            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                            return new WebResourceResponse("image/png", "utf-8", bais);
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
        js = new JsBridge(this, new JsBridge.IJsCallback() {
            @Override
            public void close() {
                hideAdv();
            }

            @Override
            public void loadFinish() {
                requestAdv();
            }

            @Override
            public void loadAdvFinish() {
                L.e("广告加载完成");
                NewsInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wv.loadUrl("javascript:showAdvPage()");//详情页面显示广告iframe
                        App.getInstance().runOnUiThread(2000, new Runnable() {
                            @Override
                            public void run() {
                                L.e("原始开始获取广告宽高");
                                wv.loadUrl("javascript:getAdvRealWidthHeight()");//广告页获取页面宽高
                            }
                        });
                    }
                });
            }

            @Override
            public void getAdvWidthHeight(final int width, final int height) {
                L.e("收到广告位宽高回调");
                NewsInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wv.loadUrl("javascript:setAdvWidthHeight("+width+","+height+")");//广告页获取页面宽高
                    }
                });
            }
        });
        wv.addJavascriptInterface(js, "carnews");
    }

    private void showAdv(String html, int width, int height){
        int newHeight = width <= 0 ? height : (js.getPageWidth() * height / width);
        String newHtml = "javascript:showAdv(\""+html+"\", "+js.getPageWidth()+","+newHeight+")";
        L.e("插入广告html："+newHtml);
        wv.loadUrl(newHtml);
        App.getInstance().runOnUiThread(1000, new Runnable() {
            @Override
            public void run() {
                wv.loadUrl("javascript:setIFrameParentWindow()");//调用后js会自动回调loadAdvFinish()方法
            }
        });
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NewsInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        wv.loadUrl("javascript:printSource()");
                    }
                });

            }
        }.start();
    }

    private void hideAdv() {
        wv.loadUrl("javascript:closeAdv()");
    }

    private void initScroll() {
        findViewById(R.id.titleInfoParent).setBackgroundColor(Color.WHITE);
        scrollControlView = (ScrollControlFrameLayout) findViewById(R.id.newsInfo_scf);
        content = findViewById(R.id.newsInfo_content);

        ((LinearLayout.LayoutParams) statusBar.getLayoutParams()).height = DensityUtils.getStatusBarHeight(this);
        top.measure(View.MeasureSpec.makeMeasureSpec(DensityUtils.getScreenWidthPixels(this), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(DensityUtils.getScreenHeightPixels(this), View.MeasureSpec.AT_MOST));
        TitleBarScrollController controller = new TitleBarScrollController(wv);
        wv.setScrollListener(controller);
        ((FrameLayout.LayoutParams) content.getLayoutParams()).topMargin = top.getMeasuredHeight();
        scrollControlView.setAlphaView(top);
        scrollControlView.setScrollView(content);
        scrollControlView.setScrollEndListener(controller);
    }

    private void requestInfo(int newId) {
        RequestUtil.requestByGetAsy(this, Urls.getUrlInfo(newId), NewsInfoBean.class, new IRequestCallBack<NewsInfoBean>() {

            @Override
            public void onSuccess(String url, NewsInfoBean data) {
                if (!ResUtils.processResponse(url, data, this)) {
                    return;
                }
                if (data == null || data.getData() == null || data.getData().size() < 1 || (info = data.getData().get(0)) == null) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getCode(), NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getMsg());
                    return;
                }
                info.setAid(newsId);
                //TODO 加载页面
                String content = info.getContent();
                try {
                    String html = FileUtils.readTextInputStream(NewsInfoActivity.this.getAssets().open("info" + File.separator + "info_template.html"));
                    html = html.replace("%1$s", content == null ? "" : content);
                    html = html.replace("%2$s", info.getTitle() == null ? "" : info.getTitle());
                    html = html.replace("%3$s", info.getPublish_source() == null ? "" : info.getPublish_source());
                    html = html.replace("%4$s", info.getPublish_time() == null ? "" : info.getPublish_time());
                    L.e(html);
                    float size = (SPBase.getInt(Const.SPKey.TEXT_SIZE, 1) - 1) * 0.2f + 1;
                    L.e("字体大小："+size);
                    js.setFontSize(size);
                    wv.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                L.e("详情请求失败");
            }

            @Override
            public void onCancel(String url) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        if (info != null)
            if (tgbtnFav.isChecked() && !hasStored)
                LoaderUtil.get().insertOrUpdate(this, info);
            else if (hasStored && !tgbtnFav.isChecked())
                LoaderUtil.get().deleteNews(this, String.valueOf(info.getAid()));
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        UMShareAPI.get(this).release();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleInfo_img_back://返回
                this.onBackPressed();
                break;
            case R.id.bottomBar_img_share://分享
            case R.id.titleInfo_img_more:
                if (info == null){
                    return;
                }
                shareUtils.shareUrl(NewsInfoActivity.this, bottomBar, "http://www.baidu.com", info.getTitle(), info.getContent(), R.mipmap.car_title_logo, new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Toast.makeText(NewsInfoActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Toast.makeText(NewsInfoActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Toast.makeText(NewsInfoActivity.this, "分享已取消", Toast.LENGTH_SHORT).show();
                    }
                }, new ShareBoardView.IOnBoardDismissListener() {
                    @Override
                    public void onDismiss(boolean isDismiss) {
                        if (isDismiss){
                            contentCover.setAnimation(animShareClose);
                            contentCover.invalidate();
                            animShareClose.startNow();
                        }else{
                            contentCover.setVisibility(View.VISIBLE);
                            ((ViewGroup)contentCover.getParent().getParent()).invalidate();
                            contentCover.setAnimation(animShareOpen);
                            contentCover.invalidate();
                            animShareOpen.startNow();
                        }
                    }
                });
                break;
        }
    }

    private void requestAdv() {
        if (TextUtils.isEmpty(channel)){
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final AdSlotType slotType = getSlotTypeByChannel(channel);
                if (slotType == null){
                    return;
                }

                AdvRequestBean req = AdvRequestUtil.getAdvRequest(NewsInfoActivity.this, slotType);
                L.e("广告请求："+new Gson().toJson(req));
                final int advWidth = js.getPageWidth();
                final int advHeight = slotType.getHeight() * advWidth / slotType.getWidth();
                String html = "";
                String response = "";
                if (slotType == AdSlotType.BANNER_800_120){
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                }else if (slotType == AdSlotType.BANNER_440_160){
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                }else if (slotType == AdSlotType.BANNER_640_100){
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                }else if (slotType == AdSlotType.BANNER_640_200){
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                }else if (slotType == AdSlotType.BANNER_240_180_W){
                    response = "{\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDT2J5OEpiUEt4Q3dvTUZRR0p6NW9qMD0QACDIECjbiJr5prH4mTMw5vLwls8rOg4I9iEQ_egEGMTiAyCFJEDYBFgxYnZodHRwczovL21lbmdqdS1zdGMtaDUud2VpbW9iLmNvbS9wL29ubGluZS9kMmUzZDQzYjhlMzNmNTc2MjRkYmIyZDkwNzNjMWJjOS8wLmh0bWw_Y2hhbm5lbFNvdXJjZT1jcGMtNDgzMjUxNTU5Jm1qX3NpZD0xaAFwAIAByBCIAej-ApABAZgBBrAB6Qc=\",\"desc\": \"\\u6c34\\u679c\\u7f51\\u8d2d\\u9996\\u9009\\uff0c\\u54c1\\u8d28\\u4fdd\\u8bc1\\uff0c\\u65b0\\u9c9c\\u5230\\u5bb6\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/1b69463d-2798-46b1-9091-aab2ba209b17.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDT2J5OEpiUEt4Q3dvTUZRR0p6NW9qMD0Q24ia-aax-JkzGg4I9iEQ_egEGMTiAyCFJCDm8vCWzyso2AQ4yBBAyBCYAQaoAekHsAEBuAEAwAHo_gLIAQE=&seqs=0\"],\"title\": \"\\u590f\\u5b63\\u6c34\\u679c\\u9c9c\\u6ecb\\u5473\\uff0c\\u9650\\u65f6\\u6298\\u4e0a\\u6298\"},\"ad_type\": 2,\"adid\": 4613,\"landing_page\":\"https://mengju-stc-h5.weimob.com/p/online/d2e3d43b8e33f57624dbb2d9073c1bc9/0.html?channelSource=cpc-483251559&mj_sid=1\"}]}";
                }else if (slotType == AdSlotType.BANNER_1000_500_W){
                    L.e("100*500先不调");
                    return;
                }
                html = AdvRequestUtil.getHtmlByResponse(NewsInfoActivity.this, js.getPageWidth(), slotType.getImgW(), slotType.getImgH(), slotType.getIconW(), slotType.getIconH(), slotType, response);
                final String finalHtml = html;
                NewsInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdvData(finalHtml, advWidth, advHeight);
                    }
                });

                /*AdvRequestUtil.requestAdv(NewsInfoActivity.this, js.getPageWidth(), req, new IAdvRequestCallback() {
                    @Override
                    public void onGetAdvSuccess(String html, int width, int height) {
                        L.e("广告请求成功");

                        processAdvData(html, width, height);
                    }

                    @Override
                    public void onGetAdvFailure() {
                        L.e("广告请求失败");
                        File file = FileUtils.createFile(NewsInfoActivity.this, FileUtils.getCachePath(NewsInfoActivity.this) + File.separator + "info", "info_adv.html");
                        try {
                            if (file == null) {
                                L.e("广告缓存文件不存在");
                                return;
                            }
                            FileUtils.writeTextFile(file, "<p>广告请求失败</p>");
                            L.e("开始显示广告:"+file.getAbsolutePath());
                            L.e("开始显示广告:"+Uri.fromFile(file).toString());
                            showAdv(Uri.fromFile(file).toString(), js.getPageWidth(), 60);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });*/
            }
        }.start();

    }

    private void processAdvData(String html, int width, int height){
        File file = FileUtils.createFile(NewsInfoActivity.this, FileUtils.getCachePath(NewsInfoActivity.this) + File.separator + "info", "info_adv.html");
        try {
            if (file == null) {
                return;
            }
            File parent = file.getParentFile();
            html = html.replaceAll("file:///android_assets", Uri.fromFile(parent).toString());
            FileUtils.writeTextFile(file, html);
            FileUtils.releaseAssets(NewsInfoActivity.this, "axd", FileUtils.getCachePath(NewsInfoActivity.this) + File.separator + "info");
            L.e("adv Html:"+html);
            showAdv(Uri.fromFile(file).toString(), width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AdSlotType getSlotTypeByChannel(String channel){
        String[] channels = this.getResources().getStringArray(R.array.tab_channels);
        List<AdSlotType> banners = AdSlotType.getBannerList();
        for (int index = 0; index < channels.length; index++){
            if (channel.equals(channels[index])){
                int i = index % banners.size();
                return banners.get(i);
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
