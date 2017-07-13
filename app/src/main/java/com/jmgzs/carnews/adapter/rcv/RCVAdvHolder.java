package com.jmgzs.carnews.adapter.rcv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.bean.AdvDataBean;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.NewsInfoActivity;
import com.jmgzs.carnews.ui.WebViewActivity;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.io.IOException;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class RCVAdvHolder extends BaseHolder<NewsDataBean> {


    private WebView wv;
    private float scale;
    private int advWidth, advHeight;

    public RCVAdvHolder(ViewGroup parent, @LayoutRes int layout, final JsBridge.IJsCallback callback) {
        super(parent, layout);
        wv = getView(R.id.item_adv_wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(false);
        wv.getSettings().setLoadsImagesAutomatically(true);
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
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra(WebViewActivity.INTENT_URL, url);
                        getContext().startActivity(intent);
                    }
                    return true;
                }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//接受证书
//                handler.cancel();//默认方式,白页
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                L.e("旧缩放比："+oldScale+"\t新缩放比："+newScale);
                scale = newScale;
                if (advWidth > 0 && advHeight > 0){
                    setAdvWidthHeight(advWidth , advHeight, newScale);
                }
            }
        });
        JsBridge js = new JsBridge((Activity) getContext(), new JsBridge.IJsCallback() {
            @Override
            public void close() {
                if (callback !=null){
                    callback.close();
                }
            }

            @Override
            public void loadFinish() {
            }

            @Override
            public void loadAdvFinish() {
//                L.e("广告加载完成");
                wv.loadUrl("javascript:getWidthHeight()");
                App.getInstance().runOnUiThread(500, new Runnable() {
                    @Override
                    public void run() {
                        wv.setVisibility(View.VISIBLE);
                        if (callback !=null){
                            callback.loadFinish();
                        }
                        wv.loadUrl("javascript:show()");
                    }
                });
            }

            @Override
            public void getAdvWidthHeight(int width, int height) {
                advWidth = width;
                advHeight = height;
                if (scale <= 0) {
                    scale = wv.getScale();
                }
//                L.e("原始广告宽高："+width+"\t"+height);
                setAdvWidthHeight(width, height, scale);
            }
        });
        wv.addJavascriptInterface(js, "carnews");
    }

    @Override
    public void setData(NewsDataBean data) {
        if (data instanceof AdvDataBean) {
            AdvDataBean adv = (AdvDataBean) data;
            String html = adv.getHtml();
            wv.getLayoutParams().width = adv.getAdvW();
            wv.getLayoutParams().height = adv.getAdvH();
//            wv.loadUrl("https://www.baidu.com");

            File file = new File(adv.getFile());
            L.e("文件路径:"+Uri.fromFile(file.getParentFile()).toString());
            L.e("html:"+html);
            wv.loadDataWithBaseURL(Uri.fromFile(file.getParentFile()).toString(), html, "text/html", "utf-8", null);
        }

    }

    private void setAdvWidthHeight(int width, int height, float scale){
        wv.getLayoutParams().width = (int) (width * scale);
        wv.getLayoutParams().height = (int) (height * scale);
        wv.invalidate();
//        L.e("缩放后广告webview宽高："+(int) (width * scale)+"\t"+(int) (height * scale));
    }
}
