package com.jmgzs.carnews.util;

import android.content.Context;
import android.widget.ImageView;

import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.NewsListBean;
import com.jmgzs.carnews.db.DBHelper;
import com.jmgzs.lib_network.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by mac on 17/6/19.
 * Description:
 */

public class LoaderUtil {

    public static void with(Context ct, Object url, ImageView image) {
        GlideApp.with(ct).load(url).fitCenter().into(image);

    }

    private static LoaderUtil instance;
    private ExecutorService pool;

    private LoaderUtil() {
        pool = Executors.newSingleThreadExecutor();
    }

    public static LoaderUtil get() {
        if (null == instance) {
            synchronized (LoaderUtil.class) {
                if (instance == null) {
                    instance = new LoaderUtil();
                }
            }
        }
        return instance;
    }

    public List<NewsDataBean> loadCache(final Context ct) {
//        Future<List<NewsDataBean>> f = pool.submit((new Callable<List<NewsDataBean>>() {
//            @Override
//            public List<NewsDataBean> call() throws Exception {
                DBHelper helper = getHelper(ct);
                return helper.queryNews();
//            }
//        }));
//        try {
//            return f.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }finally {
//            f.cancel(true);
//        }
//        return null;
    }

    public boolean insertOrUpdate(Context ct , NewsDataBean d){
        DBHelper db = getHelper(ct);
      return   db.insertOrUpdate(d);
    }

    public boolean insert(Context ct , NewsDataBean d){
        DBHelper db = getHelper(ct);
        return db.addNews(d);
    }

    public boolean deleteNews(Context ct ,String aid){
        DBHelper helper = getHelper(ct);
        return helper.deleteNews(aid);

    }

    public boolean deleteAllNews(Context ct){
        DBHelper helper = getHelper(ct);
        return helper.deleteAll();

    }

    public boolean hasStored(Context ct , String aid){
        DBHelper dh = getHelper(ct);
      return   dh.checkNews(aid);

    }

    private DBHelper getHelper(Context ct){
        return DBHelper.getInstance(ct);
    }

}
