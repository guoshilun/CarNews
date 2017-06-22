package com.jmgzs.carnews.wxapi;


//import com.umeng.socialize.weixin.view.WXCallbackActivity;

import android.os.Bundle;

import com.jmgzs.lib_network.utils.L;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.e("微信分享回调");
    }
}
