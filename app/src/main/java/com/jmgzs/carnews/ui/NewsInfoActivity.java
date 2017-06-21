package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.ui.view.ScrollControlFrameLayout;
import com.jmgzs.carnews.ui.view.ScrollableWebView;
import com.jmgzs.carnews.ui.view.ShareBoardView;
import com.jmgzs.carnews.ui.view.TitleBarScrollController;
import com.jmgzs.carnews.util.ResUtils;
import com.jmgzs.carnews.util.ShareUtils;
import com.jmgzs.carnews.util.T;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**新闻详情界面
 * Created by Wxl on 2017/6/12.
 */

public class NewsInfoActivity extends BaseActivity{

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

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent == null || 0 >((newsId = intent.getIntExtra(INTENT_AID,-1)))){
            Toast.makeText(this, "数据异常", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
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

    private void initButtons(){
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

    private void initFavBtn(){
        imgFav = (ImageView) findViewById(R.id.bottomBar_img_home);
        tgbtnFav = (ToggleButton) findViewById(R.id.bottomBar_tgbtn_home);
        //TODO 根据收藏与否更新按钮状态
        tgbtnFav.setChecked(false);
        imgFav.setImageResource(R.mipmap.fav_1);
        tgbtnFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    T.toastS("收藏成功");
                    imgFav.setImageResource(R.mipmap.fav_2);
                }else{
                    T.toastS("已取消收藏");
                    imgFav.setImageResource(R.mipmap.fav_1);
                }
            }
        });
    }

    private void initOneBtn(ImageView btn, int imgRes){
        btn.setImageResource(imgRes);
        btn.setOnClickListener(this);
        btn.setBackgroundResource(R.drawable.bg_trans_to_black);
    }

    private void initTitle(){
        tvTitle = (TextView) findViewById(R.id.titleInfo_tv_title);
        tvTitle.setText("");
    }

    @Override
    protected void addPaddingAboveContentView() {
    }

    private void initShare(){
        shareUtils = new ShareUtils();
    }

    private void initWebView(){
        wv = (ScrollableWebView) findViewById(R.id.newsInfo_wv);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        js = new JsBridge();
        wv.addJavascriptInterface(js, "carnews");
    }

    private void initScroll(){
        findViewById(R.id.titleInfoParent).setBackgroundColor(Color.WHITE);
        scrollControlView = (ScrollControlFrameLayout) findViewById(R.id.newsInfo_scf);
        content = findViewById(R.id.newsInfo_content);

        ((LinearLayout.LayoutParams)statusBar.getLayoutParams()).height = DensityUtils.getStatusBarHeight(this);
        top.measure(View.MeasureSpec.makeMeasureSpec(DensityUtils.getScreenWidthPixels(this), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(DensityUtils.getScreenHeightPixels(this), View.MeasureSpec.AT_MOST));
        TitleBarScrollController controller = new TitleBarScrollController(wv);
        wv.setScrollListener(controller);
        ((FrameLayout.LayoutParams)content.getLayoutParams()).topMargin = top.getMeasuredHeight();
        scrollControlView.setAlphaView(top);
        scrollControlView.setScrollView(content);
        scrollControlView.setScrollEndListener(controller);
    }

    private void requestInfo(int newId){
        RequestUtil.requestByGetAsy(this, Urls.getUrlInfo(newId), NewsDataBean.class, new IRequestCallBack<NewsDataBean>() {

            @Override
            public void onSuccess(String url, NewsDataBean data) {
                if (!ResUtils.processResponse(url, data, this)){
                    return;
                }
                //TODO 加载页面
                wv.loadUrl("");
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {

            }

            @Override
            public void onCancel(String url) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        UMShareAPI.get(this).release();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
                        if (isDismiss){
                            contentCover.setVisibility(View.GONE);
                        }else{
                            contentCover.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
        }
    }
}
