package com.jmgsz.lib.adv.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jmgsz.lib.adv.bean.AdvCacheBean;
import com.jmgsz.lib.adv.enums.AdSlotType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广告缓存池
 * Created by Wxl on 2017/7/18.
 */

public class CachePool {

    private static volatile Map<AdSlotType, List<AdvCacheBean>> cachePool;
    private static volatile CachePool instance;
    private static volatile Map<AdSlotType, Integer> poolSize;
    private volatile ICachePoolListener cachePoolListener;
    private static volatile SharedPreferences sp;
    private static volatile Gson gson;

    public static final String SP_NAME = "adv_cache";
    public static final String SP_KEY_CACHE = "cache";


    private CachePool() {
    }

    public static synchronized CachePool getInstance(Context context) {
        if (instance == null) {
            instance = new CachePool();
            init(context);
        }
        return instance;
    }

    public synchronized void setPoolSize(List<AdSlotType> types, int size) {
        for (AdSlotType type : types) {
            poolSize.put(type, size);
        }
    }

    public void setCachePoolListener(ICachePoolListener cachePoolListener) {
        this.cachePoolListener = cachePoolListener;
    }

    public static void init(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String cache = sp.getString(SP_KEY_CACHE, "");
        gson = new Gson();
        cachePool = gson.fromJson(cache, new TypeToken<Map<AdSlotType, List<AdvCacheBean>>>() {
        }.getType());
        if (cachePool == null) {
            cachePool = new HashMap<>();
        }
        for (AdSlotType type : AdSlotType.values()) {
            if (cachePool.get(type) == null) {
                cachePool.put(type, new ArrayList<AdvCacheBean>());
            }
        }
        poolSize = new HashMap<>();
        for (AdSlotType type : AdSlotType.values()) {
            poolSize.put(type, 10);
        }
    }

    public static void save() {
        sp.edit().putString(SP_KEY_CACHE, gson.toJson(cachePool)).apply();
    }

    public synchronized void add(AdSlotType type, AdvCacheBean data) {
        List<AdvCacheBean> cacheList = cachePool.get(type);
        if (cacheList.size() >= poolSize.get(type)) {
            if (cachePoolListener != null) {
                if (!cachePoolListener.onPoolFull(this, type, data)) {
                    save();
                    return;
                }
            }
        }
        cacheList.add(data);
        save();
    }

    public synchronized AdvCacheBean pop(AdSlotType type) {
        return remove(0, type);
    }

    private synchronized AdvCacheBean remove(int index, AdSlotType type) {
        List<AdvCacheBean> cacheList = cachePool.get(type);
        AdvCacheBean obj;
        File file = null;
        do {
            if (cacheList.size() < index + 1) {
                obj = null;
                break;
            }
            obj = cacheList.remove(index);
            if (!TextUtils.isEmpty(obj.getFilePath())) {
                file = new File(obj.getFilePath());
            }
        }
        while (file != null && (!file.exists() || !file.canRead()));
        save();
        return obj;
    }

    public synchronized int size(AdSlotType type) {
        List<AdvCacheBean> cacheList = cachePool.get(type);
        return cacheList.size();
    }

    public synchronized AdvCacheBean get(AdSlotType type) {
        List<AdvCacheBean> cacheList = cachePool.get(type);
        if (cacheList.size() < 1) {
            return null;
        }
        return cacheList.get(0);
    }

    interface ICachePoolListener {
        boolean onPoolFull(CachePool pool, AdSlotType type, AdvCacheBean data);
    }

}
