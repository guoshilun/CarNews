package com.jmgzs.carnews.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.util.WVDownloadListener;
import com.jmgzs.lib_network.utils.L;

import static com.jmgzs.carnews.R.id.webView;


/**
 * Created by XJ on 2016/5/27.
 */
public class WebViewActivity extends BaseActivity {

    private WebView wv;
    public static final String INTENT_URL = "url";
    private boolean fromSplash =false;

    @Override
    protected int getContent(Bundle b) {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        wv = getView(webView);
        WebSettings settings = wv.getSettings();
        //		settings.setSaveFormData(false);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSupportZoom(false);
        settings.setLoadWithOverviewMode(true);
        //		settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        wv.setWebChromeClient(new MyWebChromeClient(null));
        wv.setWebViewClient(new MyWebViewClient());

        wv.setDownloadListener(new WVDownloadListener(this));
        // android 5.0以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        initData();

        getView(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    protected void initData() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(INTENT_URL);
        fromSplash = intent.getBooleanExtra("fromSplash",false);
        L.e("广告详情的URL："+url);
        wv.setVisibility(View.INVISIBLE);
        wv.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (fromSplash){
            Intent in = new Intent(this ,MainActivity.class);
            startActivity(in);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv.removeAllViews();
        wv.destroy();
        wv = null;
    }

    private class MyWebChromeClient extends WebChromeClient {

        private ProgressBar loadingBar;

        public MyWebChromeClient(ProgressBar loadingBar) {
            this.loadingBar = loadingBar;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (loadingBar != null)
                    loadingBar.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("https") || url.startsWith("http")) {
                view.loadUrl(url);
            } else {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent();
                intent.setData(uri);
                if (url.startsWith(WebView.SCHEME_TEL)) {
                    intent.setAction(Intent.ACTION_DIAL);
                } else if (url.startsWith("sms") || url.startsWith(WebView.SCHEME_MAILTO)) {
                    intent.setAction(Intent.ACTION_SENDTO);
                } else if (url.startsWith("market")) {
                    // 测试跳到APP详情页面
                    intent.setAction(Intent.ACTION_VIEW);
                } else if (url.startsWith("mjsdk:")) {
                    intent.setAction(Intent.ACTION_VIEW);
                    String h5ID = uri.getQueryParameter("id");
                    Log.e("WebView test", "h5 id ===" + h5ID);
                } else {
                    intent.setAction(Intent.ACTION_VIEW);
                }
                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return true;

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("WebView test", "web view error :" + errorCode + description);
//            showFailView();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }
}
