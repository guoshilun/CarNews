package com.jmgsz.lib.adv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.jmgsz.lib.adv.bean.AdvCacheBean;
import com.jmgsz.lib.adv.bean.AdvResponseBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvStatusCallback;
import com.jmgsz.lib.adv.js.AdvJs;
import com.jmgsz.lib.adv.ui.AdvDialog;
import com.jmgsz.lib.adv.ui.WebViewActivity;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgsz.lib.adv.utils.ThreadPool;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wxl on 2017/7/6.
 */

public class InsertAdvUtil {

    private AdvJs js;
    private AdvDialog mAdvDialog;
    private int mAdvW, mAdvH;//广告页面中获取到的宽高
    private Map<String, Integer> mAdSlotTypeMap = new HashMap<>();//广告与点击类型映射

    public void showDialog(final Activity activity, final int templateId, final AdSlotType slotType, AdvCacheBean data, final IAdvStatusCallback callback) {
        if (slotType == null || data == null) {
            return;
        }
        if (mAdvDialog != null){//上一个dialog未关闭
            mAdvDialog.dismiss();
        }
        js = new AdvJs(new AdvJs.IJsCallback() {
            @Override
            public void close() {
                if (mAdvDialog != null) {
                    mAdvDialog.dismiss();
                }
                if (callback != null){
                    callback.close(templateId);
                }
            }

            @Override
            public void loadAdvFinish() {
                if (slotType != AdSlotType.INSERT_80_80_W) {
                    ThreadPool.getInstance().runOnMainThread(500, new Runnable() {
                        @Override
                        public void run() {
                            mAdvDialog.showWebView();
                        }
                    });
                }
            }

            @Override
            public void getAdvWidthHeight(int width, int height) {
                mAdvW = width;
                mAdvH = height;

                float scale;
                if (mAdvDialog.getScale() > 0) {
                    scale = mAdvDialog.getScale();
                } else {
                    scale = mAdvDialog.getWebViewScale();
                }
                L.e("设置的插屏广告宽高：" + (mAdvW * scale) + "\t" + (mAdvH * scale));
                mAdvDialog.changeWidthHeight((int) (mAdvW * scale), (int) (mAdvH * scale));

            }
        });

        final int width = DensityUtils.getScreenWidthPixels(activity) * slotType.getWidth() / slotType.getStandardWidth();
        AdvResponseBean.AdInfoBean info = data.getResponse().getAd_info().get(0);
        mAdSlotTypeMap.put(info.getLanding_page(), info.getAd_type());
        final String finalHtml = data.getHtml();
        final String htmlFile = data.getFilePath();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                L.e("插屏广告宽高：" + slotType.getWidth() + "\t" + slotType.getHeight());
//                showAdv(finalHtml, slotType.getWidth(), slotType.getHeight());
                int height = slotType.getHeight() * DensityUtils.getScreenWidthPixels(activity) / slotType.getStandardWidth();
                showAdv(activity, finalHtml, new File(htmlFile), width,  height);
            }
        });
    }

    private void showAdv(final Activity activity, final String html, final File htmlFile, final int width, final int height) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (isActivityDestroyed(activity)) {
            return;
        }
        if (mAdvDialog == null) {
            mAdvDialog = new AdvDialog(activity);
            mAdvDialog.setWidthHeight(width, height);//纯图广告直接设置宽高
        } else {
            mAdvDialog.changeWidthHeight(width, height);//纯图广告直接设置宽高
        }
        mAdvDialog.setListener(new AdvDialog.IOnAdvLoadListener() {
            @Override
            public void onAdvLoad(final WebView wv) {
                wv.addJavascriptInterface(js, "adv_js");
                L.e(html);
                wv.loadDataWithBaseURL(Uri.fromFile(htmlFile.getParentFile()).toString(), html, "text/html", "utf-8", null);
            }

            @Override
            public void onAdvScaleChanged(float newScale) {
                if (mAdvW > 0 && mAdvH > 0) {
                    L.e("设置的插屏广告宽高：" + (mAdvW * mAdvDialog.getScale()) + "\t" + (mAdvH * mAdvDialog.getScale()));
                    mAdvDialog.changeWidthHeight((int) (mAdvW * mAdvDialog.getScale()), (int) (mAdvH * mAdvDialog.getScale()));
                }
            }

            @Override
            public boolean intentToLandPage(String url) {
                Integer type;
                if ((type = mAdSlotTypeMap.get(url)) != null && type == 0) {//下载
                    return false;
                } else {//外链
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.INTENT_URL, url);
                    activity.startActivity(intent);
                    return true;
                }
            }
        });
        mAdvDialog.show();
    }

    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isActivityDestroyed(Activity activity) {
        if (activity.isDestroyed()) {
            return true;
        }
        return false;
    }

    public boolean isDialogShown() {
        return mAdvDialog != null && mAdvDialog.isShowing();
    }

    public void dismiss(){
        if (mAdvDialog != null && mAdvDialog.isShowing()){
            mAdvDialog.dismiss();
        }
    }
}
