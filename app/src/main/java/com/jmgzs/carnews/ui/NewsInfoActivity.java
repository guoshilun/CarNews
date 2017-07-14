package com.jmgzs.carnews.ui;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
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
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.NetworkErrorCode;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib_network.utils.NetworkUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private ProgressBar mPgbarLoading;
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
    private Map<String, Integer> mAdSlotTypeMap = new HashMap<>();//广告与点击类型映射

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

        boolean fromNotify = intent.getBooleanExtra("fromNotify", false);
        if (fromNotify)
            UmengUtil.event(this, UmengUtil.U_NOTIFY);
//        L.e(images.toString());
        top = findViewById(R.id.newsInfo_top_bar);
        statusBar = findViewById(R.id.newInfo_status_bar);
        bottomBar = findViewById(R.id.newsInfo_bottom_bar);
        contentCover = findViewById(R.id.newsInfo_grey_cover);
        mPgbarLoading = (ProgressBar) findViewById(R.id.newsInfo_progress_bar);

        initButtons();
        initTitle();
        initWebView();
        initScroll();
        initShare();

        requestInfo(newsId);

//        wv.loadDataWithBaseURL("file:///android_asset/", htmlTemplate, "text/html", "utf-8", null);
    }

    private void initAnim() {
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
                        ((ViewGroup) contentCover.getParent().getParent()).invalidate();
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
                UmengUtil.event(NewsInfoActivity.this, UmengUtil.U_STORE_BTN);
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
        wv.getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wv.getSettings().setAllowFileAccessFromFileURLs(true);
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100){
                    mPgbarLoading.setProgress(100);
                    Animation anim = AnimationUtils.loadAnimation(NewsInfoActivity.this, R.anim.anim_progress_hide);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mPgbarLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mPgbarLoading.setAnimation(anim);
                    anim.startNow();
                }else{
                    mPgbarLoading.setProgress(newProgress);
                }
            }
        });
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("url:" + url);
                if (url.startsWith("file")) {
                    return false;
                }
                if (url.startsWith("http")) {
                    Integer type;
                    if ((type = mAdSlotTypeMap.get(url)) != null && type == 0){//下载
                        return false;
                    }else{//外链
                        Intent intent = new Intent(NewsInfoActivity.this, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.INTENT_URL, url);
                        NewsInfoActivity.this.startActivity(intent);
                        return true;
                    }
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
                if (App.isMobile && !url.contains("mj-img") && !url.startsWith("file://")) {
                    int networkState = NetworkUtils.getNetworkState(NewsInfoActivity.this);
                    if (networkState == NetworkUtils.NETWORN_MOBILE_2G || networkState == NetworkUtils.NETWORN_MOBILE_3G || networkState == NetworkUtils.NETWORN_MOBILE_4G) {
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ((BitmapDrawable) NewsInfoActivity.this.getResources().getDrawable(R.mipmap.app_default_middle)).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
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
                L.e("详情页加载完成");
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
                        wv.loadUrl("javascript:setAdvWidthHeight(" + width + "," + height + ")");//广告页获取页面宽高
                    }
                });
            }
        });
        wv.addJavascriptInterface(js, "carnews");
    }

    private void showAdv(String htmlPath, int width, int height) {
        int newHeight = width <= 0 ? height : (js.getPageWidth() * height / width);
        String newHtml = "javascript:showAdv(\"" + htmlPath + "\", " + js.getPageWidth() + "," + newHeight + ")";
        L.e("插入广告html：" + newHtml);
        wv.loadUrl(newHtml);
        App.getInstance().runOnUiThread(2000, new Runnable() {
            @Override
            public void run() {
                wv.loadUrl("javascript:setIFrameParentWindow()");//调用后js会自动回调loadAdvFinish()方法
            }
        });
        new Thread() {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((LinearLayout.LayoutParams) statusBar.getLayoutParams()).height = DensityUtils.getStatusBarHeight(this);
        } else {
            statusBar.setVisibility(View.GONE);
        }
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
                    L.e("字体大小：" + size);
                    js.setFontSize(size);
                    File tempFile = new File(FileUtils.getCachePath(NewsInfoActivity.this) + File.separator + "info", "info.html");
                    html = AdvRequestUtil.transferHtmlToLocal(NewsInfoActivity.this, tempFile, html);
                    L.e("aaaa:" + Uri.fromFile(tempFile.getParentFile()).toString());
                    wv.loadDataWithBaseURL(Uri.fromFile(tempFile.getParentFile()).toString(), html, "text/html", "utf-8", null);
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
    protected String getUmengKey() {
        return UmengUtil.U_NEWS_DETAIL;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleInfo_img_back://返回
                this.onBackPressed();
                break;
            case R.id.bottomBar_img_share://分享
            case R.id.titleInfo_img_more:
                UmengUtil.event(NewsInfoActivity.this, UmengUtil.U_SHARE);
                if (info == null) {
                    return;
                }
                shareUtils.shareUrl(NewsInfoActivity.this, bottomBar, "http://mjcrawl-1252328573.file.myqcloud.com/44f642a2f2d6a2b861793f708b48d1201app-release.apk", Html.fromHtml(info.getTitle()), Html.fromHtml(info.getContent()), R.mipmap.car_title_logo, new UMShareListener() {
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
                        if (isDismiss) {
                            contentCover.setAnimation(animShareClose);
                            contentCover.invalidate();
                            animShareClose.startNow();
                        } else {
                            contentCover.setVisibility(View.VISIBLE);
                            ((ViewGroup) contentCover.getParent().getParent()).invalidate();
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
        if (TextUtils.isEmpty(channel)) {
            return;
        }
        final AdSlotType slotType = getSlotTypeByChannel(channel);
        if (slotType == null) {
            return;
        }
        AdvRequestBean req = AdvRequestUtil.getAdvRequest(NewsInfoActivity.this, slotType);
        L.e("广告请求：" + new Gson().toJson(req));
        final int advWidth = js.getPageWidth();
        final int advHeight = slotType.getHeight() * advWidth / slotType.getWidth();
        File dir = FileUtils.createDir(FileUtils.getCachePath(NewsInfoActivity.this) + File.separator + "info");
        AdvRequestUtil.requestAdv(NewsInfoActivity.this, js.getPageWidth(), true, req, dir.getAbsolutePath(), new IAdvRequestCallback() {
            @Override
            public void onGetAdvSuccess(String html, File localFile, int width, int height, String landPageUrl, int adType) {
                L.e("广告请求成功");
                mAdSlotTypeMap.put(landPageUrl, adType);
                processAdvData(html, width, height);
            }

            @Override
            public void onGetAdvFailure() {
                L.e("广告请求失败");
                /*String html = "";
                String response = "";
                if (slotType == AdSlotType.BANNER_800_120) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.BANNER_440_160) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.BANNER_640_100) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.BANNER_640_200) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.BANNER_240_180_W) {
                    response = "{\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDT2J5OEpiUEt4Q3dvTUZRR0p6NW9qMD0QACDIECjbiJr5prH4mTMw5vLwls8rOg4I9iEQ_egEGMTiAyCFJEDYBFgxYnZodHRwczovL21lbmdqdS1zdGMtaDUud2VpbW9iLmNvbS9wL29ubGluZS9kMmUzZDQzYjhlMzNmNTc2MjRkYmIyZDkwNzNjMWJjOS8wLmh0bWw_Y2hhbm5lbFNvdXJjZT1jcGMtNDgzMjUxNTU5Jm1qX3NpZD0xaAFwAIAByBCIAej-ApABAZgBBrAB6Qc=\",\"desc\": \"\\u6c34\\u679c\\u7f51\\u8d2d\\u9996\\u9009\\uff0c\\u54c1\\u8d28\\u4fdd\\u8bc1\\uff0c\\u65b0\\u9c9c\\u5230\\u5bb6\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/1b69463d-2798-46b1-9091-aab2ba209b17.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDT2J5OEpiUEt4Q3dvTUZRR0p6NW9qMD0Q24ia-aax-JkzGg4I9iEQ_egEGMTiAyCFJCDm8vCWzyso2AQ4yBBAyBCYAQaoAekHsAEBuAEAwAHo_gLIAQE=&seqs=0\"],\"title\": \"\\u590f\\u5b63\\u6c34\\u679c\\u9c9c\\u6ecb\\u5473\\uff0c\\u9650\\u65f6\\u6298\\u4e0a\\u6298\"},\"ad_type\": 2,\"adid\": 4613,\"landing_page\":\"https://mengju-stc-h5.weimob.com/p/online/d2e3d43b8e33f57624dbb2d9073c1bc9/0.html?channelSource=cpc-483251559&mj_sid=1\"}]}";
                } else if (slotType == AdSlotType.BANNER_1000_500_W) {
                    response = "{\"id\":ebb7fbcb-01da-4255-8c87-98eedbcd2909,\"ad_info\":[{\"ad_material\":{\"app_download_url\":\"http://mj-public.weimob.com/wd/mengdian_360_4.5.3_signed_Aligned.apk\",\"app_name\":\"\\u840c\\u5e97\",\"app_pkg_name\":\"com.hs.yjseller\",\"brand\":\"\\u5fae\\u76df\\u840c\\u5e97\",\"click_url\":\"http://c.mjmobi.com/cli?info=ChhDTEtaNFpmUEt4Q3dvTUZRR0lDYS1qMD0QACC4DijbiJr5prH4mTMwspnhl88rOg4I8yEQyt0EGMTiAyD7I0C8BVgxYkRodHRwOi8vbWotcHVibGljLndlaW1vYi5jb20vd2QvbWVuZ2RpYW5fMzYwXzQuNS4zX3NpZ25lZF9BbGlnbmVkLmFwa2gBcACAAZcPiAHo_gKQAQGYAQawAQY=\",\"desc\":\"\\u708e\\u708e\\u9177\\u6691\\u4e2d\\u7684\\u590f\\u5b63\\u6e05\\u51c9\\u6c34\\u679c\\uff0c\\u4fbf\\u5b9c\\u53c8\\u65b0\\u9c9c\",\"icon\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/36cee647-e6ec-49fa-8d42-e12915d2d28f.png\",\"images\":[\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/ad3d10bb-f5e7-4abe-88e2-e3d1103df186.jpg\"],\"show_urls\":[\"http://s.mjmobi.com/imp?info=ChhDTEtaNFpmUEt4Q3dvTUZRR0lDYS1qMD0Q24ia-aax-JkzGg4I8yEQyt0EGMTiAyD7IyCymeGXzysovAU4lw9AuA6YAQaoAQawAQG4AQDAAej-AsgBAQ==&seqs=0\"],\"title\":\"\\u590f\\u5b63\\u6c34\\u679c\\u9c9c\\u6ecb\\u5473\\uff0c\\u9650\\u65f6\\u6298\\u4e0a\\u6298\"},\"ad_type\":1,\"adid\":4603,\"landing_page\":\"http://mj-public.weimob.com/wd/mengdian_360_4.5.3_signed_Aligned.apk\"}]}";
                } else if (slotType == AdSlotType.BANNER_640_100_W) {
                    response = "{\"id\":\"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"ad_info\":[{\"ad_material\":{\"click_url\":\"http://c.mjmobi.com/cli?info=ChhDT3JjNnBiUEt4Q3VvTUZRR0p5V25qMD0QACDIECjbiJr5prH4mTMw6tzqls8rOg4I9iEQ_egEGMTiAyCEJEDYBFgxYnZodHRwczovL21lbmdqdS1zdGMtaDUud2VpbW9iLmNvbS9wL29ubGluZS9kMmUzZDQzYjhlMzNmNTc2MjRkYmIyZDkwNzNjMWJjOS8wLmh0bWw_Y2hhbm5lbFNvdXJjZT1jcGMtNDgzMjUxNTU5Jm1qX3NpZD0xaAFwAIABxxCIAej-ApABAZgBBrAB6Qc=\",\"desc\":\"\\u6c34\\u679c\\u7f51\\u8d2d\\u9996\\u9009\\uff0c\\u54c1\\u8d28\\u4fdd\\u8bc1\\uff0c\\u65b0\\u9c9c\\u5230\\u5bb6\",\"images\":[\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/db62fa82-dd7e-4d32-bb13-3ecb8c790035.jpg\"],\"show_urls\":[\"http://s.mjmobi.com/imp?info=ChhDT3JjNnBiUEt4Q3VvTUZRR0p5V25qMD0Q24ia-aax-JkzGg4I9iEQ_egEGMTiAyCEJCDq3OqWzyso2AQ4xxBAyBCYAQaoAekHsAEBuAEAwAHo_gLIAQE=&seqs=0\"],\"title\":\"\\u590f\\u5b63\\u6c34\\u679c\\u9c9c\\u6ecb\\u5473\\uff0c\\u9650\\u65f6\\u6298\\u4e0a\\u6298\"},\"ad_type\":2,\"adid\":4612,\"landing_page\":\"https://mengju-stc-h5.weimob.com/p/online/d2e3d43b8e33f57624dbb2d9073c1bc9/0.html?channelSource=cpc-483251559&mj_sid=1\"}]}";
                } else {
                    return;
                }
                html = AdvRequestUtil.getHtmlByResponse(NewsInfoActivity.this, js.getPageWidth(), slotType, response, true);
                final String finalHtml = html;
                //TODO
                NewsInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdvData(finalHtml, advWidth, advHeight);
                    }
                });*/
            }
        });

    }

    private void processAdvData(String html, int width, int height) {
        File file = FileUtils.createFile(NewsInfoActivity.this, FileUtils.getCachePath(NewsInfoActivity.this) + File.separator + "info", "info_adv.html");
        AdvRequestUtil.transferHtmlToLocal(NewsInfoActivity.this, file, html);
        showAdv(Uri.fromFile(file).toString(), width, height);
    }

    private AdSlotType getSlotTypeByChannel(String channel) {
        String[] channels = this.getResources().getStringArray(R.array.tab_channels);
        List<AdSlotType> banners = AdSlotType.getBannerList();
        for (int index = 0; index < channels.length; index++) {
            if (channel.equals(channels[index])) {
                int i = index % banners.size();
                return banners.get(i);
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(9999);
            if (tasks != null && tasks.size() > 0) {
                for (ActivityManager.RunningTaskInfo task : tasks) {
                    if (task != null && this.getPackageName().equals(task.baseActivity.getPackageName())) {
                        if (NewsInfoActivity.class.getName().equals(task.baseActivity.getClassName())) {
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                        break;
                    }
                }
            }
        }
        finish();
    }
}
