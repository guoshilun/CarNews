package com.jmgzs.carnews.push;

import android.content.Context;

/**
 * Created by Wxl on 2017/6/19.
 */

public interface IPushReceiver {

    void onReceive(Context context, Object... params);
}
