package com.jmgsz.lib.adv.interfaces;

/**
 * 广告请求回调
 * Created by Wxl on 2017/6/19.
 */

public interface IAdvResponseCallback {

    void onGetAdvResponseSuccess(int width, int height, int adType, String showUrl, String clickUrl, String imgUrl, String title, String content, String desc);

    void onGetAdvResponseFailure();
}
