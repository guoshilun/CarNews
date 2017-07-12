package com.jmgsz.lib.adv.interfaces;

import java.io.File;

/**广告请求回调
 * Created by Wxl on 2017/6/19.
 */

public interface IAdvRequestCallback {

    void onGetAdvSuccess(String html, File localFile, int width, int height);
    void onGetAdvFailure();
}
