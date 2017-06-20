package com.jmgzs.carnews.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.jmgzs.carnews.util.L;

import static com.jmgzs.carnews.util.NetworkUtils.NETWORN_MOBILE;
import static com.jmgzs.carnews.util.NetworkUtils.NETWORN_NONE;
import static com.jmgzs.carnews.util.NetworkUtils.getNetworkState;

/**
 * Created by mac on 17/6/19.
 * Description:
 */

public class NetWorkReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            // Intent中ConnectivityManager.EXTRA_NO_CONNECTIVITY这个关键字表示着当前是否连接上了网络
            boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (isBreak) {
                netState = getNetworkState(context);
                L.e("net change " + netState);
            }
        }

    }

    private static int netState = NETWORN_NONE;

    public static boolean isMobile(Context ct) {
        if (ct != null && netState == NETWORN_NONE) netState = getNetworkState(ct);
        return NETWORN_MOBILE == netState;
    }
}
