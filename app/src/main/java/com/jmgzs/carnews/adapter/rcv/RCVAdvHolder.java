package com.jmgzs.carnews.adapter.rcv;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.bean.AdvDataBean;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.lib_network.utils.L;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class RCVAdvHolder extends BaseHolder<NewsDataBean> {


    private WebView wv;


    public RCVAdvHolder(ViewGroup parent, @LayoutRes int layout, int w, int h) {
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
                return true;
            }
        });
        JsBridge js = new JsBridge((Activity) getContext(), new JsBridge.IJsCallback() {
            @Override
            public void close() {

            }

            @Override
            public void loadFinish() {

            }
        });
        wv.addJavascriptInterface(js, "carnews");
    }

    @Override
    public void setData(NewsDataBean data) {
        if (data instanceof AdvDataBean) {
            AdvDataBean adv = (AdvDataBean) data;

            wv.getLayoutParams().width = adv.getAdvW();
            wv.getLayoutParams().height = adv.getAdvH();
            wv.loadUrl(adv.getHtml());
        }

    }
}
