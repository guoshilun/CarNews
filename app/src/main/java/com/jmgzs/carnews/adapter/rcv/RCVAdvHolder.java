package com.jmgzs.carnews.adapter.rcv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.interfaces.IAdvStatusCallback;
import com.jmgsz.lib.adv.ui.WebViewActivity;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.bean.AdvDataBean;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class RCVAdvHolder extends BaseHolder<NewsDataBean> {


    private WebView wv;
    private Map<String, Integer> mAdSlotTypeMap = new HashMap<>();//广告与点击类型映射
    private IAdvStatusCallback callback;

    public RCVAdvHolder(ViewGroup parent, @LayoutRes int layout, final IAdvStatusCallback callback) {
        super(parent, layout);
        wv = getView(R.id.item_adv_wv);
        this.callback = callback;
    }

    @Override
    public void setData(NewsDataBean data) {
        if (data instanceof AdvDataBean) {
            AdvDataBean adv = (AdvDataBean) data;
            if (TextUtils.isEmpty(adv.getFile()) || TextUtils.isEmpty(adv.getLandingPageUrl())){
                return;
            }
            AdvRequestUtil.initWebView(wv.getContext(), wv, false, -1, callback);
            String html = adv.getHtml();
//            wv.loadUrl("https://www.baidu.com");
            mAdSlotTypeMap.put(adv.getLandingPageUrl(), adv.getAdType());
            File file = new File(adv.getFile());
            L.e("文件路径:"+Uri.fromFile(file.getParentFile()).toString());
            L.e("html:"+html);
            wv.loadDataWithBaseURL(Uri.fromFile(file.getParentFile()).toString(), html, "text/html", "utf-8", null);
        }

    }

}
