package com.jmgzs.carnews.js;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgsz.lib.adv.utils.DeviceUtils;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.util.T;
import com.jmgzs.lib_network.utils.L;

/**js回调类
 * Created by Wxl on 2017/6/15.
 */

public class JsBridge {

    private Activity activity;
    private IJsCallback callback;

    public JsBridge(Activity activity, IJsCallback callback){
        this.activity = activity;
        this.callback = callback;
    }

    @JavascriptInterface
    public void close(){
        L.e("广告关闭");
        if (callback != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.close();
                }
            });
        }
    }

    @JavascriptInterface
    public void toast(final String msg){
        L.e("Toast:"+msg);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.toastS(msg);
            }
        });
    }

    public interface IJsCallback{
        void close();
    }
}
