package com.jmgzs.carnews.push;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Wxl on 2017/6/19.
 */

public interface IPush {

    void appInit(Context context);

    void activityInit(Activity activity);

    void setPushReceiver(IPushReceiver receiver);
}
