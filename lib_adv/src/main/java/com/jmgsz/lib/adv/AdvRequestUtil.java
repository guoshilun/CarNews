package com.jmgsz.lib.adv;

import android.content.Context;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.bean.AdvBean;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.bean.AdvResponseBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.NetworkErrorCode;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;

import java.io.IOException;

import static com.jmgsz.lib.adv.AdvUrls.API_ADV;

/**广告请求
 * Created by wxl on 17/6/19.
 * Description:
 */

public class AdvRequestUtil {

    public static void requestAdv(final Context context, final AdvRequestBean request, final IAdvRequestCallback callback){
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
                if (type.getType() == AdSlotType.TYPE_OPEN){

                }else if (type.getType() == AdSlotType.TYPE_INFO){

                }else if (type.getType() == AdSlotType.TYPE_BANNER){

                }else if (type.getType() == AdSlotType.TYPE_INSERT){

                }else{
                    onFailure(url, NetworkErrorCode.ERROR_CODE_UNKNOWN.getCode(), "未找到对应的广告位类型的模板");
                    return;
                }
                //TODO 装载html模板
                String htmlTemplate;
                try {
                    htmlTemplate = FileUtils.readTextInputStream(context.getAssets().open("x80_80_test.html"));
                    AdvBean htmlData = AdvBean.getDataByStr("{\"exposure_url\":\"http://s.mjmobi.com/imp?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQxKCr4Lun2NHIARoOCMsgEJjnBBjY1QYgriIgyOi6-ssrKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABmO8CyAEB&seqs=0\",\"btn_url\":\"http://c.mjmobi.com/cli?info=ChhDTWpvdXZyTEt4Q0tvTUZRR1AzejFwRUIQACC4DijEoKvgu6fY0cgBMMjouvrLKzoOCMsgEJjnBBjY1QYgriJA2ARYL2ISaHR0cDovL2xhaThzeS5jb20vaAFwAIABlw-IAZjvApABAZgBBrAB6Qc=\",\"image\":{\"url\":\"https://mj-img.oss-cn-hangzhou.aliyuncs.com/7c4b9f4a-c14f-420d-8513-7eb5ebefefad.jpg\",\"width\":\"100%\",\"height\":\"100%\"},\"gl\":{\"logo\":{\"url\":\"file:///android_asset/public/img/wgtt.jpg\",\"w\":40,\"h\":40,\"scale\":2},\"title\":\"中草药高端护肤品牌\",\"detail\":\"喜欢, 是唯一的捷径. 我只管喜欢\",\"desc\":\"我只管喜欢, 不是因为有钱才喜欢, 是因为喜欢才有钱!\"}}");
                    L.e("读取到的html模板:"+htmlTemplate);
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.title\\s+\\}\\}", htmlData.getGl().getTitle());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.detail\\s+\\}\\}", htmlData.getGl().getDetail());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.desc\\s+\\}\\}", htmlData.getGl().getDesc());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.logo.url\\s+\\}\\}", htmlData.getGl().getLogo().getUrl());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.logo.w\\s+\\}\\}", "" + htmlData.getGl().getLogo().getW());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.logo.h\\s+\\}\\}", "" + htmlData.getGl().getLogo().getH());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.gl.logo.scale\\s+\\}\\}", "" + htmlData.getGl().getLogo().getScale());
                    htmlTemplate = htmlTemplate.replaceAll("\\{\\{\\s+axd.exposure_url\\s+\\}\\}", htmlData.getExposure_url());
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
                    callback.onGetAdvSuccess(htmlTemplate);
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
}
