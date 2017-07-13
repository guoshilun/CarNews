package com.jmgsz.lib.adv;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.bean.AdvResponseBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgsz.lib.adv.utils.DeviceUtils;
import com.jmgzs.lib_network.network.ConfigCache;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.type;
import static com.jmgsz.lib.adv.AdvUrls.API_ADV;

/**
 * 广告请求
 * Created by wxl on 17/6/19.
 * Description:
 */

public class AdvRequestUtil {
    //广告开关
    private static volatile boolean isOpenAdv = true;
    public static void setAdvOpen(boolean isOpen){
//        isOpenAdv = isOpen;
    }

    public static AdvRequestBean getAdvRequest(Context context, AdSlotType slotType) {
        AdvRequestBean req = new AdvRequestBean();
        req.setId("ebb7fbcb-01da-4255-8c87-98eedbcd2909");
        req.setUser_agent("Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

        //app信息
        AdvRequestBean.AppSiteInfoBean site = new AdvRequestBean.AppSiteInfoBean();
        site.setAppsite_id("51e71da6-5b46-4d9f-b94f-9ec6a");
        site.setCategories(Arrays.asList(0));
        site.setApp_name(getApplicationName(context));
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

        req.setChannel_id(1001);

        return req;
    }

    private static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public static void requestOpenAdv(final Context context, final IRequestCallBack<AdvResponseBean.AdInfoBean> callback) {
        if (!isOpenAdv){
            callback.onFailure(null, 0, "cache is null");
            return;
        }
        final AdSlotType type = AdSlotType.OPEN_640_960;
        String json = ConfigCache.getUrlCacheDefault(context, API_ADV + type.name());
//        final AdSlotType type = AdSlotType.getRandomOpenScreenType();
//        String json = ConfigCache.getUrlCache(context, API_ADV + type.name());
        final Gson gson = new Gson();
        try {
            if (json == null) throw new NullPointerException();
            AdvResponseBean.AdInfoBean cacheBean = gson.fromJson(json, AdvResponseBean.AdInfoBean.class);
            callback.onSuccess(null, cacheBean);
        } catch (Exception e) {
            callback.onFailure(null, 0, "cache is null");
//            callback.onSuccess(null,new AdvResponseBean.AdInfoBean());
        }
        RequestUtil.requestByPostAsy(context, API_ADV,
                gson.toJson(getAdvRequest(context, type)),
                AdvResponseBean.class,
                new IRequestCallBack<AdvResponseBean>() {
                    @Override
                    public void onSuccess(String url, AdvResponseBean data) {
                        if (data == null || data.getAd_info() == null || data.getAd_info().size() < 1 || (data.getAd_info().get(0)) == null) {
                            return;
                        }
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

    /**
     * 缓存广告临时文件名的index的类型映射
     */
    private static Map<Integer, Integer> tempFileIndexMap;

    private synchronized static int getTempFileIndex(int type){
        int id = tempFileIndexMap.get(type);
        tempFileIndexMap.put(type, id >= 1000 ? 0 : id+1);
        return id;
    }

    private synchronized static void initTempHtmlFileIndex(final String tempDir){
        if (tempFileIndexMap == null){
            tempFileIndexMap = new HashMap<>();
        }else{
            return;
        }
        File dir = new File(tempDir);
        File[] files = dir.listFiles();
        for (int index = 0; index < files.length; index++){
            File tempFile = files[index];
            String name = tempFile.getName();
            String regex = "(\\d{1,2})_(\\d+)\\.html";
            Matcher matcher = Pattern.compile(regex).matcher(name);
            if (matcher.find()){
                String[] typeIndex = name.replaceAll(regex, "$1_$2").split("_");
                if (typeIndex != null && typeIndex.length == 2){
                    String typeStr = typeIndex[0];
                    String indexStr = typeIndex[1];
                    try {
                        int type = Integer.parseInt(typeStr);
                        int id = Integer.parseInt(indexStr);
                        Integer indexNum = tempFileIndexMap.get(type);
                        if (indexNum != null){
                            int oldId = indexNum;
                            if (id > oldId){
                                tempFileIndexMap.put(type, id);
                            }
                        }else{
                            tempFileIndexMap.put(type, id);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        continue;
                    }
                }

            }
        }
    }
    /**
     * 请求广告
     * @param context
     * @param pageWidth WebView的宽度
     * @param isIFrame 是否为嵌入的iframe
     * @param request 请求
     * @param callback 回调
     */
    public static void requestAdv(final Context context, final int pageWidth, final boolean isIFrame, final AdvRequestBean request, final String tempDir, final IAdvRequestCallback callback) {
        if (!isOpenAdv){
            return;
        }
        //TODO 请求广告数据
        RequestUtil.requestByPostAsy(context, API_ADV, new Gson().toJson(request), AdvResponseBean.class, new IRequestCallBack<AdvResponseBean>() {
            @Override
            public void onSuccess(String url, final AdvResponseBean data) {
                L.e("广告请求接口成功返回");
                if (data == null || data.getAd_info() == null || data.getAd_info().size() < 1 || (data.getAd_info().get(0)) == null) {
                    onFailure(url, NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getCode(), NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getMsg());
                    return;
                }
                //TODO 选择模板
                final AdSlotType type = AdSlotType.getWidthHeightByTemplateId(request.getTemplate_id().get(0));
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
                new Thread(){
                    @Override
                    public void run() {
                        String htmlTemplate = getHtmlByResponseObject(context, pageWidth, type, data, isIFrame);
                        initTempHtmlFileIndex(tempDir);
                        final File tempFile = new File(tempDir, type.getType() + "_" + getTempFileIndex(type.getType()) + ".html'");
                        htmlTemplate = transferHtmlToLocal(context, tempFile, htmlTemplate);
                        final String finalHtmlTemplate = htmlTemplate;
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(finalHtmlTemplate)){
                                    if (callback != null) {
                                        callback.onGetAdvFailure();
                                    }
                                    return;
                                }

                                //TODO 返回数据
                                if (callback != null) {
                                    callback.onGetAdvSuccess(finalHtmlTemplate, tempFile, width, height);
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
                    callback.onGetAdvFailure();
                }
            }

            @Override
            public void onCancel(String url) {
                if (callback != null) {
                    callback.onGetAdvFailure();
                }
            }
        });

    }

    public synchronized static String transferHtmlToLocal(Context context, File localFile, String html){
        try {
            if (localFile == null) {
                return "";
            }
            FileUtils.createFile(context, localFile.getParentFile().getAbsolutePath(), localFile.getName());
            File parent = localFile.getParentFile();
            html = html.replaceAll("file:///android_assets", Uri.fromFile(parent).toString());
            FileUtils.writeTextFile(localFile, html);
            FileUtils.releaseAssets(context, "axd", FileUtils.getCachePath(context) + File.separator + "info");
            L.e("adv Html:"+html);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getHtmlByResponse(Context context, final int pageWidth, AdSlotType type, String response, boolean isIFrame){
        AdvResponseBean data = new Gson().fromJson(response, AdvResponseBean.class);
        return getHtmlByResponseObject(context, pageWidth, type, data, isIFrame);
    }

    public static String getHtmlByResponseObject(Context context, final int pageWidth, AdSlotType type, AdvResponseBean resObj, boolean isIFrame){
        final AdvResponseBean.AdInfoBean adInfoBean = resObj.getAd_info().get(0);
        String templateName = getTemplateId(type);
        try {
            String htmlTemplate = FileUtils.readTextInputStream(context.getAssets().open("axd" + File.separator + templateName));
//                    AdvBean htmlData = AdvBean.getDataByStr("{\"exposure_url\":\"http://s.mjmobi.com/imp?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIgyOi6-ssrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\",\"btn_url\":\"http://c.mjmobi.com/cli?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQACC4DijEoKvgu6fY0cgBMMjouvrLKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"image\":{\"url\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\",\"width\":\"100%\",\"height\":\"100%\"},\"gl\":{\"logo\":{\"url\":\"file:///android_asset/public/img/wgtt.jpg\",\"w\":40,\"h\":40,\"scale\":2},\"title\":\"中草药高端护肤品牌\",\"detail\":\"喜欢, 是唯一的捷径. 我只管喜欢\",\"desc\":\"我只管喜欢, 不是因为有钱才喜欢, 是因为喜欢才有钱!\"}}");
            L.e("读取到的html模板:"+htmlTemplate);
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.title\\s*\\}\\}", adInfoBean.getAd_material().getTitle() == null ? "" : adInfoBean.getAd_material().getTitle());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.detail\\s*\\}\\}", adInfoBean.getAd_material().getContent() == null ? "" : adInfoBean.getAd_material().getContent());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.desc\\s*\\}\\}", adInfoBean.getAd_material().getDesc() == null ? "" : adInfoBean.getAd_material().getDesc());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0) == null ? "" : adInfoBean.getAd_material().getImages().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.width\\s*\\}\\}", type.getImgW() + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*width\\s*\\}\\}", pageWidth+"");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.height\\s*\\}\\}", type.getImgH() + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0) == null ? "" : adInfoBean.getAd_material().getImages().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.w\\s*\\}\\}", ""+type.getIconW());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.h\\s*\\}\\}", "" + type.getIconH());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.btn_url\\s*\\}\\}", adInfoBean.getAd_material().getClick_url() == null ? "" : adInfoBean.getAd_material().getClick_url());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.exposure_url\\s*\\}\\}", adInfoBean.getAd_material().getShow_urls().get(0) == null ? "" : adInfoBean.getAd_material().getShow_urls().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.js\\s*\\}\\}", isIFrame ? "axd_iframe.js" : "axd_root.js");
            L.e("读取到的html模板2:"+htmlTemplate);
            return htmlTemplate;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getTemplateId(AdSlotType type) {
        Map<Integer, String> templateNameMap = new HashMap<>();
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

        return templateNameMap.get(type.getTemplateId());
    }
}
