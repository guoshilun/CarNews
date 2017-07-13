package com.jmgzs.carnews.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.NewsStoreActivity;
import com.jmgzs.carnews.ui.WebViewActivity;
import com.jmgzs.carnews.ui.dialog.AdvDialog;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wxl on 2017/7/6.
 */

public class InsertAdvUtil {

    private Activity activity;
    private JsBridge js;
    private AdvDialog mAdvDialog;
    private WebView mWv;
    private int mAdvW, mAdvH;//广告页面中获取到的宽高
    private AdSlotType mAdvType;
    private Map<String, Integer> mAdSlotTypeMap = new HashMap<>();//广告与点击类型映射

    public InsertAdvUtil(Activity activity) {
        this.activity = activity;
    }

    public void requestAdv() {
        final AdSlotType slotType = AdSlotType.getRandomInsertType();
        if (slotType == null) {
            return;
        }
        mAdvType = slotType;
        js = new JsBridge(activity, new JsBridge.IJsCallback() {
            @Override
            public void close() {
                if (mAdvDialog != null) {
                    mAdvDialog.dismiss();
                }
            }

            @Override
            public void loadFinish() {
            }

            @Override
            public void loadAdvFinish() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (slotType == AdSlotType.INSERT_80_80_W) {
                            mWv.loadUrl("javascript:getWidthHeight()");//图文调用获取页面宽高的方法设置WebView宽高
                        }
                        App.getInstance().runOnUiThread(500, new Runnable() {
                            @Override
                            public void run() {
                                mWv.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }

            @Override
            public void getAdvWidthHeight(int width, int height) {
                mAdvW = width;
                mAdvH = height;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float scale;
                        if (mAdvDialog.getScale() > 0) {
                            scale = mAdvDialog.getScale();
                        } else {
                            scale = mWv.getScale();
                        }
                        L.e("设置的插屏广告宽高：" + (mAdvW * scale) + "\t" + (mAdvH * scale));
                        mAdvDialog.changeWidthHeight((int) (mAdvW * scale), (int) (mAdvH * scale));
                    }
                });

            }
        });

        AdvRequestBean req = AdvRequestUtil.getAdvRequest(activity, slotType);
        L.e("广告请求：" + new Gson().toJson(req));
        final int width = DensityUtils.getScreenWidthPixels(activity) * slotType.getWidth() / slotType.getStandardWidth();
        File file = FileUtils.createDir(FileUtils.getCachePath(activity) + File.separator + "info");
        AdvRequestUtil.requestAdv(activity, width, false, req, file.getAbsolutePath(), new IAdvRequestCallback() {

            @Override
            public void onGetAdvSuccess(String html, File localFile, int w, int height, String landingPageUrl, int adType) {
                mAdSlotTypeMap.put(landingPageUrl, adType);
                final String finalHtml = html;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.e("插屏广告宽高：" + slotType.getWidth() + "\t" + slotType.getHeight());
//                showAdv(finalHtml, slotType.getWidth(), slotType.getHeight());
                        int height = slotType.getHeight() * DensityUtils.getScreenWidthPixels(activity) / slotType.getStandardWidth();
                        showAdv(finalHtml, width, height);
                    }
                });
            }

            @Override
            public void onGetAdvFailure() {
                //失败后显示假数据
                /*String html;
                String response;
                if (slotType == AdSlotType.INSERT_480_800) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.INSERT_600_500) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.INSERT_640_960) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.INSERT_600_600) {
                    response = "{\"ad_info\": [{\"ad_material\": {\"click_url\": \"http://c.mjmobi.com/cli?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QACC4DijEoKvgu6fY0cgBMO2e5ZvMKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"images\": [\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\"],\"show_urls\": [\"http://s.mjmobi.com/imp?info=ChhDTzJlNVp2TUt4Q2hvTUZRR0xIVXFTTT0QxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIg7Z7lm8wrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\"]},\"ad_type\": 2,\"adid\": 4398,\"landing_page\": \"http://lai8sy.com/\"}],\"id\": \"ebb7fbcb-01da-4255-8c87-98eedbcd2909\"}";
                } else if (slotType == AdSlotType.INSERT_80_80_W) {
                    response = "{\"id\":ebb7fbcb-01da-4255-8c87-98eedbcd2909,\"ad_info\":[{\"ad_material\":{\"click_url\":\"http://c.mjmobi.com/cli?info=ChhDT09iazVmUEt4Q3dvTUZRR043VnZUMD0QACDIECjbiJr5prH4mTMw45uTl88rOg4I9yEQ_egEGMTiAyCGJEC8BVgxYnZodHRwczovL21lbmdqdS1zdGMtaDUud2VpbW9iLmNvbS9wL29ubGluZS9kMmUzZDQzYjhlMzNmNTc2MjRkYmIyZDkwNzNjMWJjOS8wLmh0bWw_Y2hhbm5lbFNvdXJjZT1jcGMtNzA5NjgzODM1Jm1qX3NpZD0xaAFwAIAByRCIAej-ApABAZgBBrAB6Qc=\",\"content\":\"\\u708e\\u708e\\u9177\\u6691\\u4e2d\\u7684\\u590f\\u5b63\\u6e05\\u51c9\\u6c34\\u679c\\uff0c\\u4fbf\\u5b9c\\u53c8\\u65b0\\u9c9c\",\"desc\":\"\\u6c34\\u679c\\u7f51\\u8d2d\\u9996\\u9009\\uff0c\\u54c1\\u8d28\\u4fdd\\u8bc1\\uff0c\\u65b0\\u9c9c\\u5230\\u5bb6\",\"images\":[\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/27c7167c-fcce-4ad1-908b-97469cd6c953.jpg\"],\"show_urls\":[\"http://s.mjmobi.com/imp?info=ChhDT09iazVmUEt4Q3dvTUZRR043VnZUMD0Q24ia-aax-JkzGg4I9yEQ_egEGMTiAyCGJCDjm5OXzysovAU4yRBAyBCYAQaoAekHsAEBuAEAwAHo_gLIAQE=&seqs=0\"],\"title\": \"\\u590f\\u5b63\\u6c34\\u679c\\u9c9c\\u6ecb\\u5473\\uff0c\\u9650\\u65f6\\u6298\\u4e0a\\u6298\"},\"ad_type\":2,\"adid\":4614,\"landing_page\" :\"https://mengju-stc-h5.weimob.com/p/online/d2e3d43b8e33f57624dbb2d9073c1bc9/0.html?channelSource=cpc-709683835&mj_sid=1\"}]}";
                } else {
                    return;
                }
                html = AdvRequestUtil.getHtmlByResponse(activity, width, slotType, response, false);
                final String finalHtml = html;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.e("插屏广告宽高：" + slotType.getWidth() + "\t" + slotType.getHeight());
//                showAdv(finalHtml, slotType.getWidth(), slotType.getHeight());
                        int height = slotType.getHeight() * DensityUtils.getScreenWidthPixels(activity) / slotType.getStandardWidth();
                        showAdv(finalHtml, width, height);
                    }
                });*/
            }
        });
    }

    private void showAdv(final String html, final int width, final int height) {
        if (mAdvDialog == null) {
            mAdvDialog = new AdvDialog(activity);
            mAdvDialog.setWidthHeight(width, height);//纯图广告直接设置宽高
        } else {
            mAdvDialog.changeWidthHeight(width, height);//纯图广告直接设置宽高
        }
        mAdvDialog.setListener(new AdvDialog.IOnAdvLoadListener() {
            @Override
            public void onAdvLoad(final WebView wv) {
                wv.addJavascriptInterface(js, "carnews");
                L.e(html);
                File file = FileUtils.createFile(activity, FileUtils.getCachePath(activity) + File.separator + "info", "insert_adv.html");
                String htmlNew = AdvRequestUtil.transferHtmlToLocal(activity, file, html);
                mWv = wv;
                wv.loadDataWithBaseURL(Uri.fromFile(file.getParentFile()).toString(), htmlNew, "text/html", "utf-8", null);
            }

            @Override
            public void onAdvScaleChanged(float newScale) {
                if (mAdvW > 0 && mAdvH > 0) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            L.e("设置的插屏广告宽高：" + (mAdvW * mAdvDialog.getScale()) + "\t" + (mAdvH * mAdvDialog.getScale()));
                            mAdvDialog.changeWidthHeight((int) (mAdvW * mAdvDialog.getScale()), (int) (mAdvH * mAdvDialog.getScale()));
                        }
                    });
                }
            }

            @Override
            public boolean intentToLandPage(String url) {
                Integer type;
                if ((type = mAdSlotTypeMap.get(url)) != null && type == 0){//下载
                    return false;
                }else{//外链
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.INTENT_URL, url);
                    activity.startActivity(intent);
                    return true;
                }
            }
        });
        mAdvDialog.show();
    }

    public boolean isDialogShown() {
        return mAdvDialog != null && mAdvDialog.isShowing();
    }
}
