package com.jmgsz.lib.adv.js;

import android.webkit.JavascriptInterface;

import com.jmgsz.lib.adv.utils.ThreadPool;
import com.jmgzs.lib_network.utils.L;

/**
 * 广告页面的js相关回调
 * Created by Wxl on 2017/7/19.
 */

public class AdvJs {
    private IJsCallback callback;

    public AdvJs(IJsCallback callback) {
        this.callback = callback;
    }

    @JavascriptInterface
    public void close() {
        L.e("广告关闭");
        if (callback != null) {
            ThreadPool.getInstance().runOnMainThread(0, new Runnable() {
                @Override
                public void run() {
                    callback.close();
                }
            });
        }
    }

    @JavascriptInterface
    public void toast(final String msg) {
        L.e(msg);
    }

    @JavascriptInterface
    public void loadAdvFinish() {
        //广告页面加载完成回调原生的js
        if (callback != null) {
            ThreadPool.getInstance().runOnMainThread(0, new Runnable() {
                @Override
                public void run() {
                    callback.loadAdvFinish();
                }
            });
        }
    }

    @JavascriptInterface
    public void getAdvWidthHeight(final int width, final int height) {
        //获取广告页面宽高后的回调，如果是Webview展示直接展示广告，需要进行缩放换算，iframe展示广告则不会回调
        if (callback != null) {
            ThreadPool.getInstance().runOnMainThread(0, new Runnable() {
                @Override
                public void run() {
                    callback.getAdvWidthHeight(width, height);
                }
            });
        }
    }

    public interface IJsCallback {
        void close();

        void loadAdvFinish();

        void getAdvWidthHeight(int width, int height);
    }
}
