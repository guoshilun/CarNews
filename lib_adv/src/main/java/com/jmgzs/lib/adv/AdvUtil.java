package com.jmgzs.lib.adv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.jmgzs.lib.adv.bean.AdvCacheBean;
import com.jmgzs.lib.adv.bean.AdvRequestBean;
import com.jmgzs.lib.adv.bean.AdvResponseBean;
import com.jmgzs.lib.adv.enums.AdSlotType;
import com.jmgzs.lib.adv.interfaces.IAdvHtmlCallback;
import com.jmgzs.lib.adv.interfaces.IAdvResponseCallback;
import com.jmgzs.lib.adv.interfaces.IAdvStatusCallback;
import com.jmgzs.lib.adv.ui.AdvSplashActivity;
import com.jmgzs.lib.adv.utils.CachePool;
import com.jmgzs.lib.adv.utils.DensityUtils;
import com.jmgzs.lib.adv.utils.ThreadPool;
import com.jmgzs.lib_network.network.ConfigCache;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jmgzs.lib.adv.AdvUrls.API_ADV;

/**
 * 广告显示工具类
 * Created by wxl on 17/6/19.
 * Description:
 */

public class AdvUtil {
    //广告开关
    private static volatile boolean isOpenAdv = false;
    private static volatile InsertAdvUtil insertAdvUtil;
    private static volatile String tempDir;
    private static volatile AdvUtil instance;
    private volatile boolean isInit;

    private AdvUtil() {
    }

    public static synchronized AdvUtil getInstance() {
        if (instance == null) {
            instance = new AdvUtil();
        }
        return instance;
    }

    public static boolean isOpenAdv() {
        return isOpenAdv;
    }

    public static void setAdvOpen(boolean isOpen) {
        isOpenAdv = isOpen;
    }

    private static void init(Context context) {
        if (Thread.currentThread() != context.getMainLooper().getThread()) {
            throw new RuntimeException("Please init AdvUtil on Main Thread!");
        }
        ThreadPool.setMainHandler(context);
    }

    public void init(Context context, String tempDir){
        init(context);
        AdvUtil.tempDir = tempDir;
        if (TextUtils.isEmpty(tempDir)){//默认为应用的缓存目录下的adv文件夹
            tempDir = context.getCacheDir().getAbsolutePath() + File.separator + "adv";
        }
        File f = new File(tempDir);
        if (!f.exists()){
            f.mkdirs();
        }
        isInit = true;
    }
    private void checkInit(){
        if (!isInit){
            throw new RuntimeException("Please init before show adv!");
        }
    }

    /**
     * 有缓存则直接返回，并且请求下一次的广告缓存
     * @param context
     * @param callback
     */
    public void showOpenAdv(final Context context, final IRequestCallBack<AdvResponseBean.AdInfoBean> callback) {
        checkInit();
        if (!isOpenAdv) {
            callback.onFailure(null, 0, "cache is null");
            return;
        }
//        final AdSlotType type = AdSlotType.OPEN_640_960;
//        String json = ConfigCache.getUrlCacheDefault(context, API_ADV + type.name());
        final AdSlotType type = AdSlotType.getRandomOpenScreenType();
        String json = ConfigCache.getUrlCache(context, API_ADV + type.name());
        final Gson gson = new Gson();
        try {
            if (json == null) throw new NullPointerException();
            AdvResponseBean.AdInfoBean cacheBean = gson.fromJson(json, AdvResponseBean.AdInfoBean.class);
            callback.onSuccess(null, cacheBean);
        } catch (Exception e) {
            callback.onFailure(null, 0, "cache is null");
//            callback.onSuccess(null,new AdvResponseBean.AdInfoBean());
        }

        AdvRequestBean requestBean = AdvRequestUtil.getAdvRequest(context, type);
        L.i("request open:" + gson.toJson(requestBean));
        RequestUtil.requestByPostAsy(context, API_ADV,
                gson.toJson(requestBean),
                AdvResponseBean.class,
                new IRequestCallBack<AdvResponseBean>() {
                    @Override
                    public void onSuccess(String url, AdvResponseBean data) {
                        if (data == null || data.getAd_info() == null || data.getAd_info().size() < 1 || (data.getAd_info().get(0)) == null) {
                            return;
                        }
                        L.i("response open:" + gson.toJson(data.getAd_info().get(0)));
                        ConfigCache.setUrlCache(context, API_ADV + type.name(), gson.toJson(data.getAd_info().get(0)));
                        callback.onCancel(data.getAd_info().get(0).getAd_material().getImages().get(0));
                    }

                    @Override
                    public void onFailure(String url, int errorCode, String msg) {

                    }

                    @Override
                    public void onCancel(String url) {

                    }
                });
    }

    public void showOpenAdv(final Context context, List<Integer> templateIds, int logoResId, String appInfo, String className, Bundle params) {
        showOpenAdv(context, templateIds, logoResId, appInfo, className, params, null);
    }

    /**
     * 请求开屏广告，获取缓存并请求下一次的广告
     *
     * @param context
     * @param templateIds
     * @param callback
     */
    public void showOpenAdv(final Context context, List<Integer> templateIds, int logoResId, String appInfo, String className, Bundle params, final IAdvResponseCallback callback) {
        checkInit();
        if (!isOpenAdv) {
            callback.onGetAdvResponseFailure();
            return;
        }
        List<AdSlotType> types = new ArrayList<>();
        boolean isReturn = false;//是否成功获取到缓存数据
        for (Integer templateId : templateIds) {
            final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(templateId);
            if (type == null) {
                continue;
            }
            types.add(type);
            if (!isReturn) {
                AdvCacheBean cache = CachePool.getInstance(context).pop(type);
                if (cache != null) {
                    isReturn = true;
                    AdvResponseBean.AdInfoBean info = cache.getResponse().getAd_info().get(0);
                    AdvResponseBean.AdInfoBean.AdMaterialBean materialBean = info.getAd_material();
                    if (callback != null) {//返回缓存数据
                        callback.onGetAdvResponseSuccess(type.getWidth(), type.getHeight(), info.getAd_type(), materialBean.getShow_urls().get(0), materialBean.getClick_url(), materialBean.getImages().get(0), materialBean.getTitle(), materialBean.getContent(), materialBean.getDesc());
                    } else {
                        Intent intent = new Intent(context, AdvSplashActivity.class);
                        intent.putExtra(AdvSplashActivity.INTENT_LOGO, logoResId);
                        intent.putExtra(AdvSplashActivity.INTENT_APP_INFO, appInfo);
                        intent.putExtra(AdvSplashActivity.INTENT_PARAMS, params);
                        intent.putExtra(AdvSplashActivity.INTENT_ACTIVITY_NAME, className);
                        context.startActivity(intent);
                    }
                }
            }
            AdvRequestUtil.requestAdvToCacheNoHtml(context, type, 2);
        }
        if (!isReturn) {
            callback.onGetAdvResponseFailure();
        }
    }

    public void showInsertAdv(final Activity context, int templateId, IAdvStatusCallback callback) {
        checkInit();
        if (!isOpenAdv) {
            return;
        }
        final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(templateId);
        if (type == null) {
            return;
        }
        AdvCacheBean cache = CachePool.getInstance(context).pop(type);
        if (cache != null) {
            if (insertAdvUtil == null) {
                insertAdvUtil = new InsertAdvUtil();
            }
            insertAdvUtil.showDialog(context, templateId, type, cache, callback);
        }
        int width = type.getWidth() * DensityUtils.getScreenWidthPixels(context) / type.getStandardWidth();//换算后的广告宽度
        AdvRequestUtil.requestAdvHtmlToCache(context, type, 2, width, false, tempDir);
    }

    /**
     * 显示banner广告
     *
     * @param context
     * @param templateId
     * @param isUseCache 是否使用缓存，如果未读取到缓存，与不使用缓存方式相同
     */
    public void showBannerAdv(final Context context, int templateId, final boolean isUseCache, final boolean isIFrame, final int width, final IAdvHtmlCallback callback) {
        checkInit();
        if (!isOpenAdv) {
            return;
        }
        final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(templateId);
        if (type == null) {
            return;
        }
        if (isUseCache) {//使用缓存返回方式
            AdvCacheBean cache = CachePool.getInstance(context).pop(type);
            if (cache != null) {
                if (callback != null) {
                    AdvResponseBean.AdInfoBean info = cache.getResponse().getAd_info().get(0);
                    callback.onGetAdvHtmlSuccess(cache.getHtml(), new File(cache.getFilePath()), type.getWidth(), type.getHeight(), info.getLanding_page(), info.getAd_type());
                }
            }
            AdvRequestUtil.requestAdvHtmlToCache(context, type, 2, width, isIFrame, tempDir);
        } else {
            //直接返回请求内容方式
            AdvRequestUtil.requestAdvHtml(context, width, isIFrame, type, tempDir, false, callback);
        }
    }

    public void showBannerAdv(final Context context, final int templateId, final boolean isUseCache, final boolean isIFrame, final WebView wv, final int width, final IAdvStatusCallback callback) {
        AdvRequestUtil.initWebView(context, wv, isIFrame, templateId, callback);
        showBannerAdv(context, templateId, isUseCache, false, width, new IAdvHtmlCallback() {
            @Override
            public void onGetAdvHtmlSuccess(String html, File localFile, int width, int height, String landPageUrl, int adType) {
                wv.loadDataWithBaseURL(Uri.fromFile(localFile.getParentFile()).toString(), html, "text/html", "utf-8", null);
            }

            @Override
            public void onGetAdvHtmlFailure() {
            }
        });
    }

    public void showBannerAdv(final Context context, final int templateId, final boolean isUseCache, final IAdvResponseCallback callback) {
        checkInit();
        if (!isOpenAdv) {
            return;
        }
        final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(templateId);
        if (type == null) {
            return;
        }
        if (isUseCache) {//使用缓存返回方式
            AdvCacheBean cache = CachePool.getInstance(context).pop(type);
            if (cache != null) {
                if (callback != null) {
                    AdvResponseBean.AdInfoBean info = cache.getResponse().getAd_info().get(0);
                    AdvResponseBean.AdInfoBean.AdMaterialBean materialBean = info.getAd_material();
                    callback.onGetAdvResponseSuccess(type.getWidth(), type.getHeight(), info.getAd_type(), materialBean.getShow_urls().get(0), materialBean.getClick_url(), materialBean.getImages().get(0), materialBean.getTitle(), materialBean.getContent(), materialBean.getDesc());
                }
            }
            AdvRequestUtil.requestAdvToCacheNoHtml(context, type, 2);
        } else {
            //直接返回请求内容方式
            AdvRequestUtil.requestAdvNoHtml(context, type, isUseCache, callback);
        }
    }

    public void showInfoAdv(final Context context, int templateId, final boolean isUseCache, final IAdvResponseCallback callback) {
        showBannerAdv(context, templateId, isUseCache, callback);
    }

    public void showInfoAdv(final Context context, int templateId, final boolean isUseCache, final boolean isIFrame, final int width, final IAdvHtmlCallback callback) {
        showBannerAdv(context, templateId, isUseCache, isIFrame, width, callback);
    }

    public void showInfoAdv(final Context context, int templateId, final boolean isUseCache, boolean isIFrame, final WebView wv, final int width, final IAdvStatusCallback callback) {
        showBannerAdv(context, templateId, isUseCache, isIFrame, wv, width, callback);
    }

    public List<AdvCacheBean> getInfoAdvCacheList(final Context context, int templateId, final int width, final int count) {
        List<AdvCacheBean> data = new ArrayList<>();
        checkInit();
        if (!isOpenAdv) {
            return data;
        }
        final AdSlotType type = AdSlotType.getAdSlotTypeByTemplateId(templateId);
        if (type == null) {
            return data;
        }
        int requestCount = count;
        while (requestCount > 0) {
            AdvCacheBean cache = CachePool.getInstance(context).pop(type);
            if (cache != null) {
                data.add(cache);
                requestCount--;
            } else {
                break;
            }
        }
        AdvRequestUtil.requestAdvHtmlToCache(context, type, count, width, false, tempDir);
        return data;
    }


}
