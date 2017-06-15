package com.jmgzs.carnews.js;

import android.webkit.JavascriptInterface;

import com.jmgzs.carnews.util.L;

/**js回调类
 * Created by Wxl on 2017/6/15.
 */

public class JsBridge {

    @JavascriptInterface
    public void close(){
        L.e("广告关闭");
    }
}
