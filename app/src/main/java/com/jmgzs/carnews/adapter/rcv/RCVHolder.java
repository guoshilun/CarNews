package com.jmgzs.carnews.adapter.rcv;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.Photo;
import com.jmgzs.carnews.network.NetWorkReciver;
import com.jmgzs.carnews.util.TimeUtils;
import com.jmgzs.lib_network.utils.L;

import java.util.Collections;
import java.util.List;

import static android.R.attr.author;

/**
 * Created by mac on 17/6/12.
 * Description:无图或者3图item
 */

public class RCVHolder extends BaseHolder<NewsDataBean> {


    private ImageView image;
    private ImageView image2;
    private ImageView image3;
    private TextView title;
    private TextView author;
    private TextView time;

    private LinearLayout contentLayout;

    private GlideRequest<Drawable> request;


    public RCVHolder(ViewGroup parent, @LayoutRes int layout, int w, int h) {
        super(parent, layout);
        image = getView(R.id.item_image);
        image2 = getView(R.id.item_image2);
        image3 = getView(R.id.item_image3);
        title = getView(R.id.item_text);
        author = getView(R.id.item_author);
        time = getView(R.id.item_time);

        contentLayout = getView(R.id.item_images_layout);
        contentLayout.getLayoutParams().height = h;
        request = GlideApp.with(getContext()).asDrawable().centerCrop().error(R.mipmap.app_default_middle);

    }

    @Override
    public void setData(NewsDataBean data) {
        if (data == null) return;
        title.setText(data.getTitle());
        author.setText(data.getPublish_source());
        time.setText(TimeUtils.getTimeFromDateString(data.getPublish_time()));
        if (data.getImg_list().size() < 3) {
            contentLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            if ((App.isMobile && NetWorkReciver.isMobile(getContext()))) {
                image.setImageResource(R.mipmap.app_default_middle);
                image2.setImageResource(R.mipmap.app_default_middle);
                image3.setImageResource(R.mipmap.app_default_middle);
            } else {
                request.load(data.getImg_list().get(0)).into(image);
                request.load(data.getImg_list().get(1)).into(image2);
                request.load(data.getImg_list().get(2)).into(image3);
            }
        }
    }

}
