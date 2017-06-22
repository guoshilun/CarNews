package com.jmgzs.carnews.push;

/**
 * Created by Wxl on 2017/6/19.
 */

public class PushUtil {

    private static transient IPush mPush;

        public static synchronized IPush getPush(){
            if (mPush == null){
                mPush = new YMPushService();
            }
            return mPush;
        }

}
