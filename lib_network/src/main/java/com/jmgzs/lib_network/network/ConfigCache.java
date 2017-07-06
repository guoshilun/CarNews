

package com.jmgzs.lib_network.network;

import android.content.Context;
import android.text.TextUtils;

import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib_network.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;

public class ConfigCache {
    private static final String TAG = ConfigCache.class.getName();

    public static final int CONFIG_CACHE_MOBILE_TIMEOUT = 3600000; // 1 hour
    public static final int CONFIG_CACHE_WIFI_TIMEOUT = 300000 * 6; // 30 minute
    public static final int CONFIG_CACHE_DAY_TIMEOUT = 3600000 * 24; // 0.5 day ms

    public static String getUrlCache(Context context, String url) {
        if (url == null) {
            return null;
        }
        String result = null;

        File file = new File(FileUtils.getCachePath(context) + File.separator + replaceUrlWithPlus(url));
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();
            // 1. in case the system time is incorrect (the time is turn back
            // long ago)
            // 2. when the network is invalid, you can only read the cache
            int  mNetWorkState = NetworkUtils.getNetworkState(context.getApplicationContext());
            if (mNetWorkState != NetworkUtils.NETWORN_NONE && expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
                return null;
            }
            if (mNetWorkState == NetworkUtils.NETWORN_WIFI && expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
                return null;
            } else if (expiredTime > CONFIG_CACHE_DAY_TIMEOUT) {
                return null;
            }
            try {
                result = FileUtils.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        L.i("cache :"+result);
        return result;
    }

    /**
     * 缓存一直有效的
     *
     * @param url
     * @return
     */
    public static String getUrlCacheDefault(Context context, String url) {
        if (url == null) {
            return null;
        }
        String result = null;
        File file = new File(FileUtils.getCachePath(context) + File.separator + replaceUrlWithPlus(url));
        if (file.exists() && file.isFile()) {
            try {
                result = FileUtils.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        L.i("default cache :"+result);
        return result;
    }

    /**
     * 缓存数据
     * @param url
     * @param data
     */
    public static void setUrlCache(Context context, String url, String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        File file = new File(FileUtils.getCachePath(context) + File.separator + replaceUrlWithPlus(url));
        if (file.exists()) {
            file.delete();
        }
        try {
            // 创建缓存数据到磁盘，就是创建文件
            FileUtils.writeTextFile(file, data);
        } catch (IOException e) {
            L.i(TAG, "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            L.i(TAG, "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        }
    }

    public static void delCache(Context context, String url) {
        FileUtils.deleteFile(FileUtils.getCachePath(context) + replaceUrlWithPlus(url));

    }

    public static String replaceUrlWithPlus(String url) {
        // 1. 处理特殊字符
        // 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
        if (url != null) {
            return url.replaceAll("http://(.)*?/", "")
                    .replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }
}
