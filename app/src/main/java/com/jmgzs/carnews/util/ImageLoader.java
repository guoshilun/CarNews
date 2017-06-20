package com.jmgzs.carnews.util;

import android.content.Context;
import android.widget.ImageView;

import com.jmgzs.carnews.base.GlideApp;

/**
 * Created by mac on 17/6/19.
 * Description:
 */

public class ImageLoader {

    public static void with(Context ct , Object url , ImageView image){
        GlideApp.with(ct).load(url).fitCenter().into(image);

    }


}
