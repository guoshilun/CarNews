package com.jmgzs.carnews.network.update;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.webkit.MimeTypeMap;

import com.jmgzs.carnews.util.GetPathFromUri4kitkat;
import com.jmgzs.lib_network.utils.L;

import java.io.File;

import static com.umeng.analytics.pro.x.P;


/**
 * Created by XJ on 2017/3/28.
 */

public class UpdateDownloadListener {

    private Context ct;
    private static final Uri CONTENT = Uri.parse("content://downloads/my_downloads");
    private DownloadChangeObserver observer;

    public UpdateDownloadListener(Context context) {
        this.ct = context;
    }

    public void onDownloadStart(String url, String title, String contentDisposition) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ct.startActivity(intent);
        } else
            startDownload(url, title, contentDisposition);

    }

    private void startDownload(String url, String title, String des) {
        String fileName = "";
        if (url.contains(".apk")) {
            fileName = url.substring(0, url.lastIndexOf(".apk") + 4);
            fileName = fileName.substring(url.lastIndexOf("/") + 1);
            L.e("dlfile==" + fileName + ",title=" + title);
            String[] dlFileName = getDownloadedFileName();
            if (fileName.equalsIgnoreCase(dlFileName[0])
                    || (title != null && title.length() > 0 && title.equals(dlFileName[0]))) {
                Intent intent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                intent.putExtra(DownloadManager.COLUMN_LOCAL_URI, dlFileName[1]);
                ct.sendBroadcast(intent);
                return;
            }
        } else
            fileName = url.substring(url.lastIndexOf("/") + 1) + ".apk";

        L.e("filename=" + fileName);

        DownloadManager dm = (DownloadManager) ct.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mtm = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));//"application/vnd.android.package-archive"
        request.setMimeType(mtm);
        request.setTitle(title);
        request.setDescription(des);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(ct,
                Environment.DIRECTORY_DOWNLOADS, fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        //检测是否开启系统下载服务
        String sysPkg = "com.android.providers.downloads";
        int state = ct.getApplicationContext().getPackageManager().getApplicationEnabledSetting(sysPkg);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            try {
                Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                in.setData(Uri.parse("package:" + sysPkg));
                ct.startActivity(in);
            } catch (ActivityNotFoundException e) {
                Intent in2 = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                ct.startActivity(in2);
            }
        } else {
            long enqueue = dm.enqueue(request);
            observer = new DownloadChangeObserver(null, enqueue);
            ct.getContentResolver().registerContentObserver(CONTENT, true, observer);
        }

    }

    private String[] getDownloadedFileName() {
        String fileName = "";
        String uri = "";
        DownloadManager downloadManager = (DownloadManager) ct.getApplicationContext()
                .getSystemService(Activity.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cr = downloadManager.query(query);
        if (cr != null && cr.getCount() > 0 && cr.isBeforeFirst()) {
            cr.moveToFirst();
            fileName = cr.getString(cr
                    .getColumnIndex(DownloadManager.COLUMN_TITLE));
            uri = cr.getString(cr.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

        }
        if (uri.length() > 0) {
            //以防下载过的文件被删除必要的判断
            String path = GetPathFromUri4kitkat.getPath(ct, Uri.parse(uri));
            if (path != null && !new File(path).exists()) {
                uri = "";
                fileName = "";
            }
        }
        L.e("download: filename=" + fileName + ",uri =" + uri);
        return new String[]{fileName, uri};
    }


    public void destroy() {
        try {
            if (ct != null && observer != null)
                ct.getContentResolver().unregisterContentObserver(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用于显示下载进度
    private class DownloadChangeObserver extends ContentObserver {

        private long id;

        public DownloadChangeObserver(Handler handler, long id) {
            super(handler);
            this.id = id;
        }

        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            DownloadManager dManager = (DownloadManager) ct.getSystemService(Context.DOWNLOAD_SERVICE);
            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                int progress = Math.round(percent * 100);
//                materialDialog.setProgress(progress);
                if (progress == 100) {
//                    materialDialog.dismiss();
                }
                L.i("progress==" + progress);
            }
        }
    }

}
