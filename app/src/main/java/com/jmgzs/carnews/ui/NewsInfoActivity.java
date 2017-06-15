package com.jmgzs.carnews.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.view.ScrollControlFrameLayout;
import com.jmgzs.carnews.ui.view.ScrollableWebView;
import com.jmgzs.carnews.ui.view.TitleBarScrollController;
import com.jmgzs.carnews.util.DensityUtils;
import com.jmgzs.carnews.util.ShareUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;

import static com.umeng.socialize.bean.SHARE_MEDIA.WEIXIN;

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
    private View btnShare, btnShareTop, btnFavoriate, btnBack;
    private JsBridge js;

    private UMShareListener mShareListener;
    private ShareUtils shareUtils;

    private String mUrl, mTitle, mDesc;

    @Override
    protected void initView() {
        top = findViewById(R.id.newsInfo_top_bar);
        statusBar = findViewById(R.id.newInfo_status_bar);
        bottomBar = findViewById(R.id.newsInfo_bottom_bar);
        btnShare = findViewById(R.id.bottomBar_img_share);
        btnFavoriate = findViewById(R.id.bottomBar_img_home);
        btnShareTop = findViewById(R.id.titleInfo_img_more);
        btnBack = findViewById(R.id.titleInfo_img_back);

        wv = (ScrollableWebView) findViewById(R.id.newsInfo_wv);
        findViewById(R.id.titleInfoParent).setBackgroundColor(Color.WHITE);
        scrollControlView = (ScrollControlFrameLayout) findViewById(R.id.newsInfo_scf);
        content = findViewById(R.id.newsInfo_content);
        initWebView();
        initScroll();
        initShare();

        wv.loadUrl("http://img1.gtimg.com/news/pics/hv1/135/102/2216/144121545.jpg");
//        wv.loadUrl("http://172.18.12.28:33032/api?template_id=14");
    }

    @Override
    protected void addPaddingAboveContentView() {
    }

    private void initShare(){
        shareUtils = new ShareUtils();
    }

    private void initWebView(){
        wv.getSettings().setJavaScriptEnabled(true);
        js = new JsBridge();
        wv.addJavascriptInterface(js, "carnews");
    }

    private void initScroll(){
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

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<Activity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
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
