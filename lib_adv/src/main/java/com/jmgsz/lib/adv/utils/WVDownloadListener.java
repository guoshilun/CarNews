package com.jmgsz.lib.adv.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;

import com.jmgzs.lib_network.utils.L;

/**
 * Created by XJ on 2017/3/28.
 */

public class WVDownloadListener implements DownloadListener {

    private Context ct;

    public WVDownloadListener(Context context) {
        this.ct = context;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ct.startActivity(intent);
        } else
            startDownload(url);
    }

    private void startDownload(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        L.e("dlfile==" + fileName);
        if (url.contains(".apk")) {
            fileName = url.substring(0, url.lastIndexOf(".apk") + 4);
            fileName = fileName.substring(url.lastIndexOf("/") + 1);
            L.e("dlfile==" + fileName);
            String[] dlFileName = getDownloadedFileName();
            if (fileName.equalsIgnoreCase(dlFileName[0])) {
                Intent intent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                intent.putExtra(DownloadManager.COLUMN_LOCAL_URI, dlFileName[1]);
                ct.sendBroadcast(intent);
                return;
            }
        } else
            fileName = url.substring(url.lastIndexOf("/") + 1)+".apk";


        DownloadManager dm = (DownloadManager) ct.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mtm = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));//"application/vnd.android.package-archive"
        request.setMimeType(mtm);
        L.e(mtm);

        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(ct,
                Environment.DIRECTORY_DOWNLOADS, fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        long enqueue = dm.enqueue(request);
        L.e(enqueue + "");

    }

    private String[] getDownloadedFileName() {
        String fileName = "";
        String uri = "";
        DownloadManager downloadManager = (DownloadManager) ct.getApplicationContext()
                .getSystemService(Activity.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cr = downloadManager.query(query);
        if (cr.moveToFirst()) {
            fileName = cr.getString(cr
                    .getColumnIndex(DownloadManager.COLUMN_TITLE));
            uri = cr.getString(cr.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

        }
        L.e(fileName + ",uri =" + uri);
        return new String[]{fileName, uri};
    }

}
