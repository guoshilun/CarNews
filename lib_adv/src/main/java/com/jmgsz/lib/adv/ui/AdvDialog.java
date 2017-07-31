package com.jmgsz.lib.adv.ui;

import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jmgsz.lib.adv.R;
import com.jmgzs.lib_network.utils.L;

/**
 * 插屏广告对话框
 * Created by Wxl on 2017/6/28.
 */

public class AdvDialog extends BaseDialog {

    private IOnAdvLoadListener mListener;
    private WebView mWv;
    private Context context;
    private float scale;

    public AdvDialog(Context context) {
        super(context);
        this.context = context;
    }

    public AdvDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setListener(IOnAdvLoadListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void create(Bundle b) {
        setContentView(R.layout.dialog_insert_adv);
        setCanceledOnTouchOutside(false);
        mWidthScale = 0.75f;
    }

    @Override
    protected void initView() {
        mWv = getView(R.id.dialogInsertAdv_wv);
        mWv.getSettings().setJavaScriptEnabled(true);
        mWv.getSettings().setUseWideViewPort(true);

        mWv.getSettings().setLoadWithOverviewMode(true);
        mWv.setHorizontalScrollBarEnabled(false);
        mWv.setVerticalScrollBarEnabled(false);
        mWv.getSettings().setLoadsImagesAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mWv.getSettings().setAllowFileAccessFromFileURLs(true);
        }

        mWv.setWebChromeClient(new WebChromeClient() {
        });
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("url:" + url);
                if (url.startsWith("file")) {
                    return false;
                }
                if (url.startsWith("http")) {
                    return mListener.intentToLandPage(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                L.e("旧缩放比：" + oldScale + "\t新缩放比：" + newScale);
                scale = newScale;
                if (mListener != null) {
                    mListener.onAdvScaleChanged(newScale);
                }
            }

            //            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                L.e("资源："+url);
//                if (url.startsWith("file:///android_assets")){
//                    String mimeType;
//                    if (url.endsWith("css")){
//                        mimeType = "text/css";
//                    }else if (url.endsWith("js")){
//                        mimeType = "application/x-javascript";
//                    }else if (url.endsWith("html") || url.endsWith("htm")){
//                        mimeType = "text/html";
//                    }else if (url.endsWith("ico")){
//                        mimeType = "image/x-icon";
//                    }else{
//                        mimeType = "";
//                    }
//                    try {
//                        L.e("assets资源："+url.substring("file:///android_assets/".length())+"\t"+mimeType);
//                        InputStream is = context.getAssets().open(url.substring("file:///android_assets/".length()));
//                        if (is == null){
//                            L.e("读取assets资源为空");
//                        }
//                        return new WebResourceResponse(mimeType,"utf-8",is);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        L.e("读取assets资源失败");
//                    }
//                }else{
//                    L.e("其他资源，默认加载");
//                }
//                return null;
//            }
        });
    }

    private int width, height;

    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void changeWidthHeight(int width, int height) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height;
        getWindow().setAttributes(lp);
        mWv.getLayoutParams().width = lp.width;
        mWv.getLayoutParams().height = lp.height;
        mWv.invalidate();
    }

    @Override
    public void show() {
        super.show();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height;
        getWindow().setAttributes(lp);
        mWv.getLayoutParams().width = lp.width;
        mWv.getLayoutParams().height = lp.height;
        if (mListener != null) {
            mListener.onAdvLoad(mWv);
        }
    }

    public void showWebView() {
        mWv.setVisibility(View.VISIBLE);
    }

    public float getWebViewScale() {
        return mWv.getScale();
    }

    public float getScale() {
        return scale;
    }

    public interface IOnAdvLoadListener {
        void onAdvLoad(WebView wv);

        void onAdvScaleChanged(float newScale);

        boolean intentToLandPage(String url);
    }
}
