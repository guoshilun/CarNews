package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.view.ScrollControlFrameLayout;
import com.jmgzs.carnews.ui.view.ScrollableWebView;
import com.jmgzs.carnews.ui.view.TitleBarScrollController;
import com.jmgzs.carnews.util.DensityUtils;
import com.jmgzs.carnews.util.ShareUtils;
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
    private ImageView btnShare, btnShareTop, btnFavoriate, btnBack;
    private TextView tvTitle;
    private JsBridge js;

    private UMShareListener mShareListener;
    private ShareUtils shareUtils;

    private String mUrl, mTitle, mDesc;

    @Override
    protected void initView() {
        top = findViewById(R.id.newsInfo_top_bar);
        statusBar = findViewById(R.id.newInfo_status_bar);
        bottomBar = findViewById(R.id.newsInfo_bottom_bar);

        initButtons();
        initTitle();
        initWebView();
        initScroll();
        initShare();

        mUrl = "http://img1.gtimg.com/news/pics/hv1/135/102/2216/144121545.jpg";
        mTitle = "测试标题";
        mDesc = "测试内容";
//        mUrl = "http://172.18.12.28:33032/api?template_id=14";
        wv.loadUrl(mUrl);
    }

    private void initButtons(){
        btnShare = (ImageView) findViewById(R.id.bottomBar_img_share);
        btnFavoriate = (ImageView) findViewById(R.id.bottomBar_img_home);
        btnShareTop = (ImageView) findViewById(R.id.titleInfo_img_more);
        btnBack = (ImageView) findViewById(R.id.titleInfo_img_back);

        initOneBtn(btnBack, R.mipmap.arrow_left);
        btnShareTop.setImageResource(R.mipmap.point);
        initOneBtn(btnShareTop, R.mipmap.point);
        btnShare.setImageResource(R.mipmap.share);
        initOneBtn(btnShare, R.mipmap.share);
        btnFavoriate.setImageResource(R.mipmap.arrow_left);
        initOneBtn(btnFavoriate, R.mipmap.arrow_left);
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
        ((LinearLayout.LayoutParams)content.getLayoutParams()).topMargin = top.getMeasuredHeight();
        scrollControlView.setAlphaView(top);
        scrollControlView.setScrollView(content);
        scrollControlView.setScrollEndListener(controller);
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
            case R.id.bottomBar_img_home://收藏
                break;
            case R.id.bottomBar_img_share://分享
            case R.id.titleInfo_img_more:
                shareUtils.shareUrl(NewsInfoActivity.this, bottomBar, mUrl, mTitle, mDesc, R.mipmap.car_title_logo, new UMShareListener() {
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
                });
                break;
        }
    }
}
