package com.jmgzs.carnews.network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.jmgzs.carnews.util.FileProvider7;
import com.jmgzs.lib_network.utils.L;

import java.io.File;

import static android.R.attr.type;
import static android.content.ContentValues.TAG;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_MOBILE_2G;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_MOBILE_3G;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_MOBILE_4G;
import static com.jmgzs.lib_network.utils.NetworkUtils.NETWORN_NONE;
import static com.jmgzs.lib_network.utils.NetworkUtils.getNetworkState;


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
        } else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            long uid = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
            String uri = intent.getStringExtra(DownloadManager.COLUMN_LOCAL_URI);
            L.syso("broadcast id==" + uid + "uri:" + uri);
            if (uri != null) {
                installApk(context, uri);
            } else if (uid != -1) {
                installApk(context, uid);
            }
        }

    }

    private static int netState = NETWORN_NONE;

    public static boolean isMobile(Context ct) {
        if (ct != null && netState == NETWORN_NONE) netState = getNetworkState(ct);
        if (netState == NETWORN_MOBILE_2G || netState == NETWORN_MOBILE_3G || netState == NETWORN_MOBILE_4G) {
            return true;
        } else {
            return false;
        }
    }

    // 安装Apk
    private void installApk(Context context, long uid) {
        DownloadManager dm = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        String uri = null;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(uid);
        Cursor cr = dm.query(query);
        if (cr.moveToFirst()) {
            int column = cr.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (cr.getInt(column) == DownloadManager.STATUS_SUCCESSFUL) {
                uri = (cr.getString(cr.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            }
        }
        if (uri != null)
            installApk(context, uri);
    }

    private void installApk(Context context, String uri) {
        try {
            L.e(uri != null ? uri : "null apk");
            Intent i = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File f = new File(uri);
                Uri u = FileProvider7.getUriForFile(context, f);
                String type = context.getContentResolver().getType(u);
                i.setDataAndType(u, type);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                i.setDataAndType(Uri.parse(uri), "application/vnd.android.package-archive");
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            Log.e(TAG, "安装失败");
            e.printStackTrace();
        }

    }
}
