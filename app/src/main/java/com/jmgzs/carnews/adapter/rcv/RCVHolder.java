package com.jmgzs.carnews.adapter.rcv;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.Photo;

import java.util.Collections;
import java.util.List;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class RCVHolder extends BaseHolder<NewsDataBean>{
//        implements ListPreloader.PreloadModelProvider<Photo>, ListPreloader.PreloadSizeProvider<Photo> {


    private ImageView image;
    private ImageView image2;
    private ImageView image3;
    private TextView title;

    private int[] actualSize;

    private GlideRequest<Drawable> request;
    private Key signature;


    public RCVHolder(ViewGroup parent, @LayoutRes int layout) {
        super(parent, layout);
        image = getView(R.id.item_image);
        image2 = getView(R.id.item_image2);
        image3 = getView(R.id.item_image3);
        title = getView(R.id.item_text);
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (actualSize == null)
                    actualSize = new int[]{image.getWidth(), image.getHeight()};
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        request = GlideApp.with(getContext()).asDrawable().centerCrop();

    }

    @Override
    public void setData(NewsDataBean data) {
        if (data == null) return;
        this.dataBean = data;
        title.setText(data.getTitle());

//        signature = new ObjectKey(data);
        request.
                load(data.getPhoto().get(0).getUrl()).
//                placeholder(R.mipmap.ic_launcher).
//                error(R.mipmap.ic_launcher).
//                signature(signature).
//                diskCacheStrategy(DiskCacheStrategy.ALL).
                into(image);
        request.load(data.getPhoto().get(0).getUrl()).into(image2);
        request.load(data.getPhoto().get(0).getUrl()).into(image3);
    }

    private NewsDataBean dataBean;

    public List<Photo> getPreloadItems(int position) {
        return (dataBean.getPhoto());
    }

    public RequestBuilder getPreloadRequestBuilder(Photo item) {
        return request.clone().signature(signature).load(item.getUrl());
    }

    @Nullable
    public int[] getPreloadSize(Photo item, int adapterPosition, int perItemPosition) {
        return actualSize;
    }
}
