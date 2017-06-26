package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
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
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.NewsInfoBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.ui.view.ScrollControlFrameLayout;
import com.jmgzs.carnews.ui.view.ScrollableWebView;
import com.jmgzs.carnews.ui.view.ShareBoardView;
import com.jmgzs.carnews.ui.view.TitleBarScrollController;
import com.jmgzs.carnews.util.LoaderUtil;
import com.jmgzs.carnews.util.ResUtils;
import com.jmgzs.carnews.util.ShareUtils;
import com.jmgzs.carnews.util.T;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.NetworkErrorCode;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 新闻详情界面
 * Created by Wxl on 2017/6/12.
 */

public class NewsInfoActivity extends BaseActivity {

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_news_info;
    }

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

    private UMShareListener mShareListener;
    private ShareUtils shareUtils;

    private int newsId;
    private String downloadUrl;
    public static final String INTENT_AID = "aid";
    private boolean hasStored = false;
    private NewsDataBean info;
    private ArrayList<String> images ;

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent == null || 0 > ((newsId = intent.getIntExtra(INTENT_AID, -1)))) {
            Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        images = intent.getStringArrayListExtra("images");
        L.e(images.toString());
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
                if (isChecked) {
                    T.toastS("收藏成功");
                    imgFav.setImageResource(R.mipmap.fav_2);
                } else {
                    T.toastS("已取消收藏");
                    imgFav.setImageResource(R.mipmap.fav_1);
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
    }

    private void initWebView() {
        wv = (ScrollableWebView) findViewById(R.id.newsInfo_wv);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(false);
        wv.getSettings().setLoadsImagesAutomatically(true);
        js = new JsBridge(this, new JsBridge.IJsCallback() {
            @Override
            public void close() {

            }
        });
        wv.addJavascriptInterface(js, "carnews");
    }

    private void showAdv(String html) {
        wv.loadUrl("javascript:showAdv(\"" + html + "\")");
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
                //TODO 加载页面
                info.setAid(newsId);
                info.setImg_list(images);
                String content = info.getContent();
                try {
                    String html = FileUtils.readTextInputStream(NewsInfoActivity.this.getAssets().open("info" + File.separator + "info_template.html"));
                    html = String.format(html, content == null ? "" : content, info.getTitle() == null ? "" : info.getTitle(), info.getPublish_source() == null ? "" : info.getPublish_source(), info.getPublish_time() == null ? "" : info.getPublish_time());
                    L.e(html);
                    wv.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
//                    requestAdv();
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
                this.finish();
                break;
            case R.id.bottomBar_img_share://分享
            case R.id.titleInfo_img_more:
                shareUtils.shareUrl(NewsInfoActivity.this, bottomBar, "http://www.baidu.com", "测试", "测试描述", R.mipmap.car_title_logo, new UMShareListener() {
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
                            contentCover.setVisibility(View.GONE);
                        } else {
                            contentCover.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
        }
    }

    private void requestAdv() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String req = "{\"id\":\"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"user_agent\":\"Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30\",\"app_site_info\":{\"appsite_id\":\"51e71da6-5b46-4d9f-b94f-9ec6a\",\"categories\":[0],\"app_bundle_name\":\"dongfangtoutiao_test\",\"app_name\":\"\"},\"net_type\":2,\"ad_slot_info\":[{\"sid\": 0,\"height\": 220,\"screen_position\": 1,\"loc_id\": \"bf8a85e6-849e-11e6-8c73-a4dcbef43d46\",\"width\": 156,\"ad_num\": 1,\"html_material\": false}],\"id_info\": {\"mac\": \"d8:55:a3:ce:e4:40\",\"idfa\": \"5b7e9e4f42a6635f\"},\"device_info\": {\"orientation\": 2,\"model\": \"MX5\",\"brand\": \"MEIXU\",\"screen_width\": 1080,\"type\": 2,\"screen_height\": 1920},\"user_ip\": \"58.30.22.0\",\"template_id\": [2044,2001],\"channel_id\": 1001}";
                AdvRequestUtil.requestAdv(NewsInfoActivity.this, new Gson().fromJson(req, AdvRequestBean.class), new IAdvRequestCallback() {
                    @Override
                    public void onGetAdvSuccess(String html) {
                        showAdv(html);
                        L.e("广告请求成功");
                    }

                    @Override
                    public void onGetAdvFailure() {
                        showAdv("<p>广告请求失败</p>");
                        L.e("广告请求失败");
                    }
                });
            }
        }.start();

    }
}
