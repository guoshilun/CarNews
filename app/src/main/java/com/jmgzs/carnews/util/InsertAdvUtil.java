package com.jmgzs.carnews.util;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.NewsStoreActivity;
import com.jmgzs.carnews.ui.dialog.AdvDialog;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.io.IOException;

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

    public InsertAdvUtil(Activity activity){
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
                        float scaleX, scaleY;
                        if (mAdvDialog.getScale() > 0) {
                            scaleX = mAdvDialog.getScale();
                            scaleY = mAdvDialog.getScale();
                        }else{
                            scaleX = mWv.getScale();
                            scaleY = mWv.getScale();
                        }
                        L.e("设置的插屏广告宽高：" + (mAdvW * scaleX) + "\t" + (mAdvH * scaleY));
                        mAdvDialog.changeWidthHeight((int) (mAdvW * scaleX), (int) (mAdvH * scaleY));
                    }
                });

            }
        });

        AdvRequestBean req = AdvRequestUtil.getAdvRequest(activity, slotType);
        L.e("广告请求：" + new Gson().toJson(req));
        String html = "";
        String response = "";
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
        final int width = DensityUtils.getScreenWidthPixels(activity) * slotType.getWidth() / slotType.getStandardWidth();
        html = AdvRequestUtil.getHtmlByResponse(activity, width, slotType.getImgW(), slotType.getImgH(), slotType.getIconW(), slotType.getIconH(), slotType, response);
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


//                String req = "{\"id\":\"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"user_agent\":\"Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30\",\"app_site_info\":{\"appsite_id\":\"51e71da6-5b46-4d9f-b94f-9ec6a\",\"categories\":[0],\"app_bundle_name\":\"dongfangtoutiao_test\",\"app_name\":\"\"},\"net_type\":2,\"ad_slot_info\":[{\"sid\": 0,\"height\": 220,\"screen_position\": 1,\"loc_id\": \"bf8a85e6-849e-11e6-8c73-a4dcbef43d46\",\"width\": 156,\"ad_num\": 1,\"html_material\": false}],\"id_info\": {\"mac\": \"d8:55:a3:ce:e4:40\",\"idfa\": \"5b7e9e4f42a6635f\"},\"device_info\": {\"orientation\": 2,\"model\": \"MX5\",\"brand\": \"MEIXU\",\"screen_width\": 1080,\"type\": 2,\"screen_height\": 1920},\"user_ip\": \"58.30.22.0\",\"template_id\": [2044],\"channel_id\": 1001}";
        /*AdvRequestBean req = AdvRequestUtil.getAdvRequest(activity, slotType);
        L.e("广告请求："+new Gson().toJson(req));
        AdvRequestUtil.requestAdv(activity, 0, req, new IAdvRequestCallback() {
            @Override
            public void onGetAdvSuccess(String html, int width, int height) {
                L.e("广告请求成功");
                L.e("adv Html:"+html);
                showAdv(html, width, height);
            }

            @Override
            public void onGetAdvFailure() {
                L.e("广告请求失败");
                showAdv("<p>广告请求失败</p>", js.getPageWidth(), 60);

            }
        });*/
    }

    private void showAdv(final String html, final int width, final int height) {
        if (mAdvDialog == null) {
            mAdvDialog = new AdvDialog(activity);
            mAdvDialog.setWidthHeight(width, height);//纯图广告直接设置宽高
        }else{
            mAdvDialog.changeWidthHeight(width, height);//纯图广告直接设置宽高
        }
        mAdvDialog.setListener(new AdvDialog.IOnAdvLoadListener() {
            @Override
            public void onAdvLoad(final WebView wv) {
                wv.addJavascriptInterface(js, "carnews");
                L.e(html);
                File file = FileUtils.createFile(activity, FileUtils.getCachePath(activity) + File.separator + "info", "insert_adv.html");
                try {
                    if (file == null) {
                        return;
                    }
                    File parent = file.getParentFile();
                    String htmlNew = html;
                    htmlNew = htmlNew.replaceAll("file:///android_assets", Uri.fromFile(parent).toString());
                    FileUtils.writeTextFile(file, htmlNew);
                    FileUtils.releaseAssets(activity, "axd", FileUtils.getCachePath(activity) + File.separator + "info");
                    L.e("adv Html:");
                    for (int i = 0; i < htmlNew.length(); i += 200) {
                        int last = i + 200 > htmlNew.length() ? htmlNew.length() : i + 200;
                        L.e(htmlNew.substring(i, last));
                    }
                    mWv = wv;
                    wv.loadDataWithBaseURL(Uri.fromFile(parent).toString(), htmlNew, "text/html", "utf-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        });
        mAdvDialog.show();
    }

    public boolean isDialogShown(){
        return mAdvDialog != null && mAdvDialog.isShowing();
    }
}
