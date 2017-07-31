package com.jmgsz.lib.adv.interfaces;

import java.io.File;

/**
 * 广告请求回调
 * Created by Wxl on 2017/6/19.
 */

public interface IAdvHtmlCallback {

    void onGetAdvHtmlSuccess(String html, File localFile, int width, int height, String landPageUrl, int adType);

    void onGetAdvHtmlFailure();
}
