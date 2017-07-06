package com.jmgzs.carnews.js;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgsz.lib.adv.utils.DeviceUtils;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.util.T;
import com.jmgzs.lib_network.utils.L;

/**
 * js回调类
 * Created by Wxl on 2017/6/15.
 */

public class JsBridge {

    private Activity activity;
    private IJsCallback callback;
    private float fontSize;
    private int pageWidth;

    public JsBridge(Activity activity, IJsCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @JavascriptInterface
    public void close() {
        L.e("广告关闭");
        if (callback != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.close();
                }
            });
        }
    }

    @JavascriptInterface
    public void toast(final String msg) {
        for (int i = 0; i < msg.length(); i += 200) {
            int last = i + 200 > msg.length() ? msg.length() : i + 200;
            L.e(msg.substring(i, last));
        }
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                T.toastS(msg);
//            }
//        });
    }

    @JavascriptInterface
    public float toast() {
        return fontSize;
    }

    @JavascriptInterface
    public float getFontSize() {
        return fontSize;
    }

    @JavascriptInterface
    public void setPageWidth(int width) {
        pageWidth = width;
    }

    @JavascriptInterface
    public void loadFinish() {
        if (callback != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.loadFinish();
                }
            });
        }
    }

    @JavascriptInterface
    public void loadAdvFinish() {
        if (callback != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.loadAdvFinish();
                }
            });
        }
    }

    @JavascriptInterface
    public void getAdvWidthHeight(final int width, final int height) {
        if (callback != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.getAdvWidthHeight(width, height);
                }
            });
        }
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public interface IJsCallback {
        void close();

        void loadFinish();

        void loadAdvFinish();

        void getAdvWidthHeight(int width, int height);
    }
}
