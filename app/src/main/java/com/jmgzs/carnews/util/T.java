package com.jmgzs.carnews.util;

import android.content.Context;
import android.widget.Toast;

import com.jmgzs.carnews.base.App;

/**
 * Created by mac on 17/6/19.
 * Description:
 */

public class T {

    public static void toastS(Context ct, CharSequence msg) {
        if (ct == null || msg == null || msg.length() == 0) return;
        Toast.makeText(ct, msg, Toast.LENGTH_SHORT).show();

    }

    public static void toastS(CharSequence msg) {
        toastS(App.getInstance(), msg);
    }


    public static void toastL(Context ct, CharSequence msg) {
        if (ct == null || msg == null || msg.length() == 0) return;
        Toast.makeText(ct, msg, Toast.LENGTH_LONG).show();
    }

    public static void toastL(CharSequence msg) {
        toastL(App.getInstance(), msg);
    }
}
