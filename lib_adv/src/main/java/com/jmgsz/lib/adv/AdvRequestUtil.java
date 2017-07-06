package com.jmgsz.lib.adv;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.bean.AdvResponseBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgsz.lib.adv.utils.DeviceUtils;
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

import static com.jmgsz.lib.adv.AdvUrls.API_ADV;

/**广告请求
 * Created by wxl on 17/6/19.
 * Description:
 */

public class AdvRequestUtil {

    public static AdvRequestBean getAdvRequest(Context context, AdSlotType slotType){
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
        req.setNet_type(NetworkUtils.getNetworkState(context)+1);

        //广告位
        AdvRequestBean.AdSlotInfoBean slot = new AdvRequestBean.AdSlotInfoBean();
        slot.setSid(0);
        slot.setHeight(slotType.getHeight());
        slot.setWidth(slotType.getWidth());
        slot.setScreen_position(1);
        slot.setLoc_id("bf8a85e6-849e-11e6-8c73-a4dcbef43d46");
        slot.setAd_num(1);
        slot.setHtml_material(false);
        slot.setProduct_type(Arrays.asList(1,2,3,4));
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
        geo.setLatitude(23.16f);
        geo.setLongitude(113.23f);
        req.setGeo_info(geo);

        req.setTemplate_id(Arrays.asList(slotType.getTemplateId()));
        req.setUser_ip(DeviceUtils.getDeviceCurrentIP());

        req.setChannel_id(1001);

        return req;
    }
    public static String getApplicationName(Context context) {
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

    /**
     *
     * @param context
     * @param windowWidth 实际webview显示的宽
     * @param request
     * @param callback
     */
    public static void requestAdv(final Context context, final int windowWidth, final AdvRequestBean request, final IAdvRequestCallback callback){
        //TODO 请求广告数据
        RequestUtil.requestByPostAsy(context, API_ADV, new Gson().toJson(request), AdvResponseBean.class, new IRequestCallBack<AdvResponseBean>() {
            @Override
            public void onSuccess(String url, AdvResponseBean data) {
                final AdvResponseBean.AdInfoBean adInfoBean;
                if (data == null || data.getAd_info() == null || data.getAd_info().size() < 1 || (adInfoBean = data.getAd_info().get(0)) == null){
                    onFailure(url, NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getCode(), NetworkErrorCode.ERROR_CODE_EMPTY_RESPONSE.getMsg());
                    return;
                }
                //TODO 选择模板
                AdSlotType type = AdSlotType.getWidthHeightByTemplateId(request.getTemplate_id().get(0));
                if (type == null){
                    onFailure(url, NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode(), "未找到templateId对应的广告位类型");
                    return;
                }
                int width = type.getWidth();
                int height = type.getHeight();

                String templateName = getTemplateId(type);
                if (TextUtils.isEmpty(templateName)){
                    onFailure(url, NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode(), "未找到对应的广告位类型的模板");
                    return;
                }
                L.e("读取广告宽高:"+width+"\t"+height);
                //TODO 装载html模板
                String htmlTemplate;
                try {
                    htmlTemplate = FileUtils.readTextInputStream(context.getAssets().open("axd"+ File.separator+templateName));
//                    AdvBean htmlData = AdvBean.getDataByStr("{\"exposure_url\":\"http://s.mjmobi.com/imp?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIgyOi6-ssrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\",\"btn_url\":\"http://c.mjmobi.com/cli?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQACC4DijEoKvgu6fY0cgBMMjouvrLKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"image\":{\"url\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\",\"width\":\"100%\",\"height\":\"100%\"},\"gl\":{\"logo\":{\"url\":\"file:///android_asset/public/img/wgtt.jpg\",\"w\":40,\"h\":40,\"scale\":2},\"title\":\"中草药高端护肤品牌\",\"detail\":\"喜欢, 是唯一的捷径. 我只管喜欢\",\"desc\":\"我只管喜欢, 不是因为有钱才喜欢, 是因为喜欢才有钱!\"}}");
                    L.e("读取到的html模板:"+htmlTemplate);
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.title\\s*\\}\\}", adInfoBean.getAd_material().getTitle());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.detail\\s*\\}\\}", adInfoBean.getAd_material().getContent());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.desc\\s*\\}\\}", adInfoBean.getAd_material().getDesc());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0));
                    int advWidth, advHeight;
                    if (windowWidth <= 0){
                        advWidth = width;
                        advHeight = height;
                    }else{
                        advWidth = windowWidth;
                        advHeight = height * windowWidth / width;
                    }
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.width\\s*\\}\\}", advWidth + "px");
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.height\\s*\\}\\}", advHeight + "px");
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.url\\s*\\}\\}", adInfoBean.getAd_material().getIcon());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.w\\s*\\}\\}", ""+type.getImgW());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.h\\s*\\}\\}", "" + type.getImgH());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.btn_url\\s*\\}\\}", adInfoBean.getAd_material().getClick_url());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.exposure_url\\s*\\}\\}", adInfoBean.getAd_material().getShow_urls().get(0));
                    L.e("读取到的html模板2:"+htmlTemplate);

                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null){
                        callback.onGetAdvFailure();
                    }
                    return;
                }
                //TODO 返回数据
                if (callback != null){
                    callback.onGetAdvSuccess(htmlTemplate, width, height);
                }
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                if (callback != null){
                    callback.onGetAdvFailure();
                }
            }

            @Override
            public void onCancel(String url) {
                if (callback != null){
                    callback.onGetAdvFailure();
                }
            }
        });

    }

    public static String getHtmlByResponse(Context context, final int pageWidth, final int imgWidth, final int imgHeight, final int iconWidth, final int iconHeight, AdSlotType type, String response){
        final AdvResponseBean.AdInfoBean adInfoBean;
        AdvResponseBean data = new Gson().fromJson(response, AdvResponseBean.class);
        adInfoBean = data.getAd_info().get(0);
        String templateName = getTemplateId(type);
        try {
            String htmlTemplate = FileUtils.readTextInputStream(context.getAssets().open("axd"+ File.separator+templateName));
//                    AdvBean htmlData = AdvBean.getDataByStr("{\"exposure_url\":\"http://s.mjmobi.com/imp?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIgyOi6-ssrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\",\"btn_url\":\"http://c.mjmobi.com/cli?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQACC4DijEoKvgu6fY0cgBMMjouvrLKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"image\":{\"url\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\",\"width\":\"100%\",\"height\":\"100%\"},\"gl\":{\"logo\":{\"url\":\"file:///android_asset/public/img/wgtt.jpg\",\"w\":40,\"h\":40,\"scale\":2},\"title\":\"中草药高端护肤品牌\",\"detail\":\"喜欢, 是唯一的捷径. 我只管喜欢\",\"desc\":\"我只管喜欢, 不是因为有钱才喜欢, 是因为喜欢才有钱!\"}}");
            L.e("读取到的html模板:"+htmlTemplate);
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.title\\s*\\}\\}", adInfoBean.getAd_material().getTitle() == null ? "" : adInfoBean.getAd_material().getTitle());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.detail\\s*\\}\\}", adInfoBean.getAd_material().getContent() == null ? "" : adInfoBean.getAd_material().getContent());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.desc\\s*\\}\\}", adInfoBean.getAd_material().getDesc() == null ? "" : adInfoBean.getAd_material().getDesc());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0) == null ? "" : adInfoBean.getAd_material().getImages().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.width\\s*\\}\\}", imgWidth + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*width\\s*\\}\\}", pageWidth+"");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.image.height\\s*\\}\\}", imgHeight + "");
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.url\\s*\\}\\}", adInfoBean.getAd_material().getImages().get(0) == null ? "" : adInfoBean.getAd_material().getImages().get(0));
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.w\\s*\\}\\}", ""+iconWidth);
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.gl.logo.h\\s*\\}\\}", "" + iconHeight);
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.btn_url\\s*\\}\\}", adInfoBean.getAd_material().getClick_url() == null ? "" : adInfoBean.getAd_material().getClick_url());
            htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s*axd.exposure_url\\s*\\}\\}", adInfoBean.getAd_material().getShow_urls().get(0) == null ? "" : adInfoBean.getAd_material().getShow_urls().get(0));
            L.e("读取到的html模板2:"+htmlTemplate);
            return htmlTemplate;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getTemplateId(AdSlotType type){
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

//        templateNameMap.put(2001, "x75_75.mustache");
        templateNameMap.put(2004, "x80_80.mustache");
        templateNameMap.put(2003, "x240_180.mustache");
        templateNameMap.put(2010, "x600_300.mustache");
        templateNameMap.put(2048, "x600_300.mustache");
        templateNameMap.put(2039, "x720_405.mustache");
        templateNameMap.put(2029, "x800_120.mustache");
        //待定
        templateNameMap.put(2061, "x600_300.mustache");

        return templateNameMap.get(type.getTemplateId());
    }
}
