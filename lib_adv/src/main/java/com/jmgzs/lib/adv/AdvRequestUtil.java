package com.jmgzs.lib.adv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.jmgzs.lib.adv.bean.AdvCacheBean;
import com.jmgzs.lib.adv.bean.AdvRequestBean;
import com.jmgzs.lib.adv.bean.AdvResponseBean;
import com.jmgzs.lib.adv.enums.AdSlotType;
import com.jmgzs.lib.adv.interfaces.IAdvHtmlCallback;
import com.jmgzs.lib.adv.interfaces.IAdvResponseCallback;
import com.jmgzs.lib.adv.interfaces.IAdvStatusCallback;
import com.jmgzs.lib.adv.js.AdvJs;
import com.jmgzs.lib.adv.ui.AdvWebViewActivity;
import com.jmgzs.lib.adv.utils.AppUtil;
import com.jmgzs.lib.adv.utils.CachePool;
import com.jmgzs.lib.adv.utils.DensityUtils;
import com.jmgzs.lib.adv.utils.DeviceUtils;
import com.jmgzs.lib.adv.utils.HtmlFileUtil;
import com.jmgzs.lib.adv.utils.ThreadPool;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.NetworkErrorCode;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib_network.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.jmgzs.lib.adv.AdvUrls.API_ADV;

/**
 * 广告请求辅助工具类
 * Created by Wxl on 2017/7/20.
 */

public class AdvRequestUtil {

    private volatile static Map<Integer, String> templateNameMap;

    static AdvRequestBean getAdvRequest(Context context, AdSlotType slotType) {
        AdvRequestBean req = new AdvRequestBean();
        req.setId("ebb7fbcb-01da-4255-8c87-98eedbcd2909");
        req.setUser_agent("Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

        //app信息
        AdvRequestBean.AppSiteInfoBean site = new AdvRequestBean.AppSiteInfoBean();
        site.setAppsite_id("51e71da6-5b46-4d9f-b94f-9ec6a");
        site.setCategories(Arrays.asList(0));
        site.setApp_name(AppUtil.getApplicationName(context));
        site.setApp_bundle_name(context.getPackageName());
        req.setApp_site_info(site);

        //网络
        req.setNet_type(NetworkUtils.getNetworkState(context) + 1);

        //广告位
        AdvRequestBean.AdSlotInfoBean slot = new AdvRequestBean.AdSlotInfoBean();
        slot.setSid(0);
        slot.setHeight(slotType.getHeight());
        slot.setWidth(slotType.getWidth());
        slot.setScreen_position(1);
        slot.setLoc_id("bf8a85e6-849e-11e6-8c73-a4dcbef43d46");
        slot.setAd_num(1);
        slot.setHtml_material(false);
        slot.setProduct_type(Arrays.asList(1, 2, 3, 4));
        req.setAd_slot_info(Arrays.asList(slot));

        //imei
        AdvRequestBean.IdInfoBean idInfo = new AdvRequestBean.IdInfoBean();
        idInfo.setImei(DeviceUtils.getIMEI(context));
        req.setId_info(idInfo);

        //Device
        AdvRequestBean.DeviceInfoBean device = new AdvRequestBean.DeviceInfoBean();
        device.setOrientation(2);
        device.setModel(DeviceUtils.getDeviceName());
        device.setBrand(DeviceUtils.getBrand());
        device.setScreen_width(DensityUtils.getScreenWidthPixels(context));
        device.setScreen_height(DensityUtils.getScreenHeightPixels(context));
        device.setType(2);
        req.setDevice_info(device);

        AdvRequestBean.GeoInfoBean geo = new AdvRequestBean.GeoInfoBean();
//        geo.setLatitude(23.16f);
//        geo.setLongitude(113.23f);
        double[] latlng = DeviceUtils.getLocation(context);
        geo.setLatitude(Double.valueOf(latlng[0]).floatValue());
        geo.setLongitude(Double.valueOf(latlng[1]).floatValue());
        req.setGeo_info(geo);

        req.setTemplate_id(Arrays.asList(slotType.getTemplateId()));
        req.setUser_ip(DeviceUtils.getDeviceCurrentIP());

        req.setChannel_id(9999);

        return req;
    }

    public static void requestAdvHtmlToCache(final Context context, final AdSlotType type, int reqCount, int width, boolean isIFrame, String tempDir) {
        requestAdv(context, type, reqCount, tempDir, true, width, isIFrame, true);
    }

    public static void requestAdvToCacheNoHtml(final Context context, final AdSlotType type, int reqCount) {
        requestAdv(context, type, reqCount, null, false, 0, false, false);
    }

    public static void requestAdv(final Context context, final AdSlotType type, final int reqCount, final String tempDir, final boolean isHtml, final int width, final boolean isIFrame, final boolean isCache) {
        ThreadPool.getInstance().runThread(new Runnable() {

            boolean isOnceFinish = false;
            int count = reqCount;
            int tryCount = reqCount * 2;

            @Override
            public void run() {
                while (tryCount-- > 0 && count > 0) {
                    isOnceFinish = false;
                    if (!isHtml) {
                        AdvRequestUtil.requestAdvNoHtml(context, type, isCache, new IAdvResponseCallback() {
                            @Override
                            public void onGetAdvResponseSuccess(int width, int height, int adType, String showUrl, String clickUrl, String imgUrl, String title, String content, String desc) {
                                count--;
                                isOnceFinish = true;
                            }

                            @Override
                            public void onGetAdvResponseFailure() {
                                isOnceFinish = true;
                            }
                        });
                    } else {
                        AdvRequestUtil.requestAdvHtml(context, width, isIFrame, type, tempDir, isCache, new IAdvHtmlCallback() {
                            @Override
                            public void onGetAdvHtmlSuccess(String html, File localFile, int width, int height, String landPageUrl, int adType) {
                                count--;
                                isOnceFinish = true;
                            }

                            @Override
                            public void onGetAdvHtmlFailure() {
                                isOnceFinish = true;
                            }
                        });
                    }

                    while (!isOnceFinish) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        });
    }

    /**
     * 请求广告，返回接口对象并缓存，非html
     *
     * @param context
     * @param type     广告类型
     * @param callback
     */
    static void requestAdvNoHtml(final Context context, final AdSlotType type, final boolean isCache, final IAdvResponseCallback callback) {
        final AdvRequestBean request = AdvRequestUtil.getAdvRequest(context, type);
        String req = new Gson().toJson(request);
        L.e("广告请求：" + req);
        //TODO 请求广告数据
        RequestUtil.requestByPostAsy(context, API_ADV, req, AdvResponseBean.class, new IRequestCallBack<AdvResponseBean>() {
            @Override
            public void onSuccess(String url, final AdvResponseBean data) {
                L.e("广告请求接口成功返回");
                if (data == null || data.getAd_info() == null || data.getAd_info().size() < 1 || (data.getAd_info().get(0)) == null || data.getAd_info().get(0).getAd_material() == null) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getCode(), NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getMsg());
                    return;
                }
                final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(request.getTemplate_id().get(0));
                if (type == null) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode(), "未找到templateId对应的广告位类型");
                    return;
                }
                final int width = type.getWidth();
                final int height = type.getHeight();

                if (isCache) {
                    AdvCacheBean cache = new AdvCacheBean(data);
                    CachePool.getInstance(context).add(type, cache);
                }

                if (callback != null) {
                    AdvResponseBean.AdInfoBean info = data.getAd_info().get(0);
                    AdvResponseBean.AdInfoBean.AdMaterialBean materialBean = info.getAd_material();
                    callback.onGetAdvResponseSuccess(width, height, info.getAd_type(), materialBean.getShow_urls().get(0), materialBean.getClick_url(), materialBean.getImages().get(0), materialBean.getTitle(), materialBean.getContent(), materialBean.getDesc());
                }
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                L.e("广告请求接口返回失败");
                if (callback != null) {
                    callback.onGetAdvResponseFailure();
                }
            }

            @Override
            public void onCancel(String url) {
                if (callback != null) {
                    callback.onGetAdvResponseFailure();
                }
            }
        });
    }

    /**
     * 请求广告，返回组装完成的html并缓存文件到传入的目录中
     *
     * @param context
     * @param pageWidth WebView的宽度
     * @param isIFrame  是否为嵌入的iframe，会使用不同的js
     * @param type      广告类型
     * @param tempDir   缓存目录名
     * @param callback  回调
     */
    static void requestAdvHtml(final Context context, final int pageWidth, final boolean isIFrame, final AdSlotType type, final String tempDir, final boolean isCache, final IAdvHtmlCallback callback) {
        final AdvRequestBean request = AdvRequestUtil.getAdvRequest(context, type);
        String req = new Gson().toJson(request);
        L.e("广告请求：" + req);
        //TODO 请求广告数据
        RequestUtil.requestByPostAsy(context, API_ADV, req, AdvResponseBean.class, new IRequestCallBack<AdvResponseBean>() {
            @Override
            public void onSuccess(String url, final AdvResponseBean data) {
                L.e("广告请求接口成功返回");
                if (data == null || data.getAd_info() == null || data.getAd_info().size() < 1 || (data.getAd_info().get(0)) == null || data.getAd_info().get(0).getAd_material() == null) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getCode(), NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getMsg());
                    return;
                }

                //TODO 选择模板
                final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(request.getTemplate_id().get(0));
                if (type == null) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode(), "未找到templateId对应的广告位类型");
                    return;
                }
                final int width = type.getWidth();
                final int height = type.getHeight();

                String templateName = getTemplateId(type);
                if (TextUtils.isEmpty(templateName)) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode(), "未找到对应的广告位类型的模板");
                    return;
                }
                L.e("读取广告宽高:" + width + "\t" + height);
                //TODO 装载html模板
                new Thread() {
                    @Override
                    public void run() {
                        //html缓存并装载数据
                        String htmlTemplate = getHtmlByResponseObject(context, pageWidth, type, data, isIFrame);
                        final File tempFile = new File(tempDir, type.getTemplateId() + "_" + HtmlFileUtil.getTempFileIndex(tempDir, type.getType()) + ".html");
                        htmlTemplate = transferHtmlToLocal(context, tempFile, htmlTemplate);
                        final String finalHtmlTemplate = htmlTemplate;
                        if (isCache) {
                            AdvCacheBean cache = new AdvCacheBean(htmlTemplate, data, tempFile.getAbsolutePath());
                            CachePool.getInstance(context).add(type, cache);
                        }
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(finalHtmlTemplate)) {
                                    if (callback != null) {
                                        callback.onGetAdvHtmlFailure();
                                    }
                                    return;
                                }

                                //TODO 返回数据
                                if (callback != null) {
                                    callback.onGetAdvHtmlSuccess(finalHtmlTemplate, tempFile, width, height, data.getAd_info().get(0).getAd_material().getShow_urls().get(0) == null ? "" : data.getAd_info().get(0).getAd_material().getShow_urls().get(0), data.getAd_info().get(0).getAd_type());
                                }
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                L.e("广告请求接口返回失败");
                if (callback != null) {
                    callback.onGetAdvHtmlFailure();
                }
            }

            @Override
            public void onCancel(String url) {
                if (callback != null) {
                    callback.onGetAdvHtmlFailure();
                }
            }
        });

    }

    public synchronized static String transferHtmlToLocal(Context context, File localFile, String html) {
        try {
            if (localFile == null) {
                return "";
            }
            String parentDirPath = localFile.getParentFile().getAbsolutePath();
            FileUtils.releaseAssets(context, "axd", parentDirPath);
            FileUtils.createFile(context, localFile.getParentFile().getAbsolutePath(), localFile.getName());
            File parent = localFile.getParentFile();
            html = html.replaceAll("file:///android_assets", Uri.fromFile(parent).toString());
            FileUtils.writeTextFile(localFile, html);

            L.e("adv Html:" + html);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    static String getHtmlByResponse(Context context, final int pageWidth, AdSlotType type, String response, boolean isIFrame) {
        AdvResponseBean data = new Gson().fromJson(response, AdvResponseBean.class);
        return getHtmlByResponseObject(context, pageWidth, type, data, isIFrame);
    }

    static String getHtmlByResponseObject(Context context, final int pageWidth, AdSlotType type, AdvResponseBean resObj, boolean isIFrame) {
        final AdvResponseBean.AdInfoBean adInfoBean = resObj.getAd_info().get(0);
        String templateName = getTemplateId(type);
        try {
            String htmlTemplate = FileUtils.readTextInputStream(context.getAssets().open("axd" + File.separator + templateName));
//                    AdvBean htmlData = AdvBean.getDataByStr("{\"exposure_url\":\"http://s.mjmobi.com/imp?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIgyOi6-ssrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\",\"btn_url\":\"http://c.mjmobi.com/cli?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQACC4DijEoKvgu6fY0cgBMMjouvrLKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"image\":{\"url\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\",\"width\":\"100%\",\"height\":\"100%\"},\"gl\":{\"logo\":{\"url\":\"file:///android_asset/public/img/wgtt.jpg\",\"w\":40,\"h\":40,\"scale\":2},\"title\":\"中草药高端护肤品牌\",\"detail\":\"喜欢, 是唯一的捷径. 我只管喜欢\",\"desc\":\"我只管喜欢, 不是因为有钱才喜欢, 是因为喜欢才有钱!\"}}");
//            L.e("读取到的html模板:" + htmlTemplate);
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.title\\s*\\}\\}", adInfoBean.getAd_material().getTitle() == null ? "" : adInfoBean.getAd_material().getTitle());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.detail\\s*\\}\\}", adInfoBean.getAd_material().getContent() == null ? "" : adInfoBean.getAd_material().getContent());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.desc\\s*\\}\\}", adInfoBean.getAd_material().getDesc() == null ? "" : adInfoBean.getAd_material().getDesc());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0) == null ? "" : adInfoBean.getAd_material().getImages().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.width\\s*\\}\\}", type.getImgW() + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*width\\s*\\}\\}", pageWidth + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.height\\s*\\}\\}", type.getImgH() + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0) == null ? "" : adInfoBean.getAd_material().getImages().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.w\\s*\\}\\}", "" + type.getIconW());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.h\\s*\\}\\}", "" + type.getIconH());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.btn_url\\s*\\}\\}", adInfoBean.getAd_material().getClick_url() == null ? "" : adInfoBean.getAd_material().getClick_url());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.exposure_url\\s*\\}\\}", adInfoBean.getAd_material().getShow_urls().get(0) == null ? "" : adInfoBean.getAd_material().getShow_urls().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.js\\s*\\}\\}", isIFrame ? "axd_iframe.js" : "axd_root.js");
//            L.e("读取到的html模板2:" + htmlTemplate);
            return htmlTemplate;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void initWebView(final Context context, final WebView wv, final boolean isIFrame, final int id, final IAdvStatusCallback callback) {
        if (wv == null) {
            return;
        }
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(true);

        wv.getSettings().setLoadWithOverviewMode(true);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setVerticalScrollBarEnabled(false);
        wv.getSettings().setLoadsImagesAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            wv.getSettings().setAllowFileAccessFromFileURLs(true);
        }

        wv.setWebChromeClient(new WrapWebChromeClient((WebChromeClient) wv.getTag(R.integer.adv_tag_web_chrome_client)));
        final float[] scale = new float[1];//当前页面缩放比
        final float[] widthHeight = new float[2];//当前html页面中的宽高
        wv.setWebViewClient(new WrapWebViewClient((WebViewClient) wv.getTag(R.integer.adv_tag_web_view_client)) {
            @Override
            public boolean shouldOverrideUrlLoading2(WebView view, String url) {
                L.e("url:" + url);
                if (url.startsWith("file")) {
                    return false;
                }
                if (url.startsWith("http")) {
                    Intent intent = new Intent(context, AdvWebViewActivity.class);
                    intent.putExtra(AdvWebViewActivity.INTENT_URL, url);
                    context.startActivity(intent);
                    return true;
                }
                return true;
            }

            @Override
            public void onReceivedSslError2(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onScaleChanged2(WebView view, float oldScale, float newScale) {
                L.e("旧缩放比：" + oldScale + "\t新缩放比：" + newScale);
                scale[0] = newScale;
                if (widthHeight[0] > 0 && widthHeight[1] > 0) {
                    if (!isIFrame) {
                        ViewGroup.LayoutParams lps = wv.getLayoutParams();
                        lps.width = (int) (widthHeight[0] * newScale);
                        lps.height = (int) (widthHeight[1] * newScale);
                        wv.setLayoutParams(lps);
                    }
                }
            }
        });

        AdvJs js = new AdvJs(new AdvJs.IJsCallback() {
            @Override
            public void close() {
                if (callback != null) {
                    callback.close(id);
                }
            }

            @Override
            public void loadAdvFinish() {
//                ThreadPool.getInstance().runOnMainThread(500, new Runnable() {
//                    @Override
//                    public void run() {
                wv.setVisibility(View.VISIBLE);
//                    }
//                });
            }

            @Override
            public void getAdvWidthHeight(final int width, final int height) {
                widthHeight[0] = width;
                widthHeight[1] = height;

                float curScale;
                if (scale[0] > 0) {
                    curScale = scale[0];
                } else {
                    curScale = wv.getScale();
                }
                if (!isIFrame) {
                    ViewGroup.LayoutParams lps = wv.getLayoutParams();
                    lps.width = (int) (width * curScale);
                    lps.height = (int) (height * curScale);
                    wv.setLayoutParams(lps);
                }
            }
        });

        wv.addJavascriptInterface(js, "adv_js");
    }

    static synchronized String getTemplateId(AdSlotType type) {
        if (templateNameMap == null) {
            templateNameMap = new HashMap<>();
            templateNameMap.put(2019, "img.mustache");
            templateNameMap.put(2032, "img.mustache");
            templateNameMap.put(2023, "img.mustache");
            templateNameMap.put(2044, "img.mustache");
            templateNameMap.put(2020, "img.mustache");
            templateNameMap.put(2021, "img.mustache");
            templateNameMap.put(2017, "img.mustache");
            templateNameMap.put(2018, "img.mustache");
            templateNameMap.put(2022, "img.mustache");
            templateNameMap.put(2033, "img.mustache");
            templateNameMap.put(2024, "img.mustache");
            templateNameMap.put(2031, "img.mustache");

            templateNameMap.put(2001, "x75_75.mustache");
            templateNameMap.put(2004, "x80_80.mustache");
            templateNameMap.put(2003, "x240_180.mustache");
            templateNameMap.put(2010, "x600_300.mustache");
//        templateNameMap.put(2048, "x600_300.mustache");
            templateNameMap.put(2039, "x720_405.mustache");
            templateNameMap.put(2029, "x800_120.mustache");
            //TODO 待定
            templateNameMap.put(2061, "x600_300.mustache");
        }
        return templateNameMap.get(type.getTemplateId());
    }
}

abstract class WrapWebViewClient extends WebViewClient {
    private WebViewClient client;

    public WrapWebViewClient(WebViewClient client) {
        super();
        this.client = client;
    }

    public abstract boolean shouldOverrideUrlLoading2(WebView view, String url);

    public abstract void onReceivedSslError2(WebView view, SslErrorHandler handler, SslError error);

    public abstract void onScaleChanged2(WebView view, float oldScale, float newScale);

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean result;
        if (!(result = shouldOverrideUrlLoading2(view, url))) {
            if (client != null) {
                return client.shouldOverrideUrlLoading(view, url);
            }
        } else {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (client != null) {
            return client.shouldOverrideUrlLoading(view, request);
        } else {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (client != null) {
            client.onPageStarted(view, url, favicon);
        } else {
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (client != null) {
            client.onPageFinished(view, url);
        } else {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (client != null) {
            client.onLoadResource(view, url);
        } else {
            super.onLoadResource(view, url);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (client != null) {
            client.onPageCommitVisible(view, url);
        } else {
            super.onPageCommitVisible(view, url);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (client != null) {
            return client.shouldInterceptRequest(view, url);
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (client != null) {
            return client.shouldInterceptRequest(view, request);
        } else {
            return super.shouldInterceptRequest(view, request);
        }
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        if (client != null) {
            client.onTooManyRedirects(view, cancelMsg, continueMsg);
        } else {
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String
            failingUrl) {
        if (client != null) {
            client.onReceivedError(view, errorCode, description, failingUrl);
        } else {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError
            error) {
        if (client != null) {
            client.onReceivedError(view, request, error);
        } else {
            super.onReceivedError(view, request, error);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest
            request, WebResourceResponse errorResponse) {
        if (client != null) {
            client.onReceivedHttpError(view, request, errorResponse);
        } else {
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (client != null) {
            client.onFormResubmission(view, dontResend, resend);
        } else {
            super.onFormResubmission(view, dontResend, resend);
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (client != null) {
            client.doUpdateVisitedHistory(view, url, isReload);
        } else {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        onReceivedSslError2(view, handler, error);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (client != null) {
            client.onReceivedClientCertRequest(view, request);
        } else {
            super.onReceivedClientCertRequest(view, request);
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String
            host, String realm) {
        if (client != null) {
            client.onReceivedHttpAuthRequest(view, handler, host, realm);
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (client != null) {
            return client.shouldOverrideKeyEvent(view, event);
        } else {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (client != null) {
            client.onUnhandledKeyEvent(view, event);
        } else {
            super.onUnhandledKeyEvent(view, event);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        onScaleChanged2(view, oldScale, newScale);
        if (client != null) {
            client.onScaleChanged(view, oldScale, newScale);
        }
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (client != null) {
            client.onReceivedLoginRequest(view, realm, account, args);
        } else {
            super.onReceivedLoginRequest(view, realm, account, args);
        }
    }
}

class WrapWebChromeClient extends WebChromeClient {
    private WebChromeClient client;

    public WrapWebChromeClient(WebChromeClient client) {
        super();
        this.client = client;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (client != null) {
            client.onProgressChanged(view, newProgress);
        } else {
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (client != null) {
            client.onReceivedTitle(view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (client != null) {
            client.onReceivedIcon(view, icon);
        } else {
            super.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (client != null) {
            client.onReceivedTouchIconUrl(view, url, precomposed);
        } else {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (client != null) {
            client.onShowCustomView(view, callback);
        } else {
            super.onShowCustomView(view, callback);
        }
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (client != null) {
            client.onShowCustomView(view, requestedOrientation, callback);
        } else {
            super.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        if (client != null) {
            client.onHideCustomView();
        } else {
            super.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (client != null) {
            return client.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        } else {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (client != null) {
            client.onRequestFocus(view);
        } else {
            super.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (client != null) {
            client.onCloseWindow(window);
        } else {
            super.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (client != null) {
            return client.onJsAlert(view, url, message, result);
        } else {
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (client != null) {
            return client.onJsConfirm(view, url, message, result);
        } else {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (client != null) {
            return client.onJsPrompt(view, url, message, defaultValue, result);
        } else {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (client != null) {
            return client.onJsBeforeUnload(view, url, message, result);
        } else {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        if (client != null) {
            client.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        } else {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (client != null) {
            client.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        } else {
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (client != null) {
            client.onGeolocationPermissionsShowPrompt(origin, callback);
        } else {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (client != null) {
            client.onGeolocationPermissionsHidePrompt();
        } else {
            super.onGeolocationPermissionsHidePrompt();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (client != null) {
            client.onPermissionRequest(request);
        } else {
            super.onPermissionRequest(request);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (client != null) {
            client.onPermissionRequestCanceled(request);
        } else {
            super.onPermissionRequestCanceled(request);
        }
    }

    @Override
    public boolean onJsTimeout() {
        if (client != null) {
            return client.onJsTimeout();
        } else {
            return super.onJsTimeout();
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (client != null) {
            client.onConsoleMessage(message, lineNumber, sourceID);
        } else {
            super.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (client != null) {
            return client.onConsoleMessage(consoleMessage);
        } else {
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (client != null) {
            return client.getDefaultVideoPoster();
        } else {
            return super.getDefaultVideoPoster();
        }
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (client != null) {
            return client.getVideoLoadingProgressView();
        } else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (client != null) {
            client.getVisitedHistory(callback);
        } else {
            super.getVisitedHistory(callback);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (client != null) {
            return client.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        } else {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    }
}
