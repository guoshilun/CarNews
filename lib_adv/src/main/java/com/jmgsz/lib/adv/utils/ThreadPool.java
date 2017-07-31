package com.jmgsz.lib.adv.utils;

import android.content.Context;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Wxl on 2017/7/18.
 */

public class ThreadPool {

    private static volatile ThreadPool instance;
    private static volatile Handler handler;
    private ExecutorService threadPool;

    private ThreadPool() {
    }

    public static synchronized ThreadPool getInstance() {
        if (instance == null) {
            instance = new ThreadPool();
        }
        return instance;
    }

    public static void setMainHandler(Context context) {
        if (handler == null) {
            handler = new Handler(context.getMainLooper());
        }
    }

    public void runThread(Runnable runnable) {
        init();
        threadPool.execute(runnable);
    }

    public void runOnMainThread(final long milliSecond, final Runnable runnable) {
        init();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, milliSecond);
    }

    private synchronized void init() {
        if (threadPool == null) {
            threadPool = Executors.newCachedThreadPool();
        }
    }

}
