package com.jmgzs.carnews.adapter.rcv;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.adapter.rcvbase.RCVBaseAdapter;
import com.jmgzs.carnews.bean.NewsDataBean;

import java.util.List;


/**
 * Created by XJ on 2016/9/23.
 */
public class RCVAdapter extends RCVBaseAdapter<NewsDataBean, RCVAdapter.Holder> {


    public RCVAdapter(List<NewsDataBean> data, OnRCVItemClickListener.OnItemClickListener itemClickListener) {
        super(data, itemClickListener);
    }

    public RCVAdapter(List<NewsDataBean> data) {
        super(data);
    }


    @Override
    public Holder onCreateVH(ViewGroup parent, int viewType) {
        return new Holder(parent, R.layout.item_view_image3);
    }



    public static class Holder extends BaseHolder<NewsDataBean> {

        private ImageView image;
        private TextView title;

        public Holder(ViewGroup parent, @LayoutRes int layout) {
            super(parent, layout);
            image = getView(R.id.item_image);
            title = getView(R.id.item_text);
//            image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
////                    L.syso("tree:"+image.getMeasuredWidth() +"-"+image.getMeasuredHeight());
//                    ViewGroup.LayoutParams params = image.getLayoutParams();
//                    int w = (DensityUtils.SCREEN_WIDTH_PIXELS - DensityUtils.dip2px(10)*4)/3;
//                    params.width = w;
//                    params.height = w ;//+ DensityUtils.dip2px(35);
//                    image.setLayoutParams(params);
//                    L.syso("tree2:"+params.width +"-"+params.height);
//
//                }
//            });
//
//            title.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
////                    L.syso("tree:"+image.getMeasuredWidth() +"-"+image.getMeasuredHeight());
//                    ViewGroup.LayoutParams params = title.getLayoutParams();
//                    int w = (DensityUtils.SCREEN_WIDTH_PIXELS - DensityUtils.dip2px(10)*4)/3;
//                    params.width = w;
//                    title.setLayoutParams(params);
//                    L.syso("tree2:"+params.width +"-"+params.height);
//
//                }
//            });
//            L.syso( +image.getMeasuredWidth() +"-"+image.getMeasuredHeight());

        }

        @Override
        public void setData(NewsDataBean data) {
            if (data == null) return;
            title.setText(data.getTitle());
//            final long start = System.currentTimeMillis();
//            picasso.load(data.getCover())
//                    //.placeholder(R.mipmap.icon)
//                    .placeholder(R.drawable.anim_loading)
////                    .placeholder(R.drawable.spinner)
//                    .error(R.mipmap.app_default_middle).fit().into(image
//            , new Callback() {
//                @Override
//                public void onSuccess() {
//                    L.e("image load time:"+(System.currentTimeMillis()-start));
//                }
//
//                @Override
//                public void onError() {
//                    L.e("image load error time:"+(System.currentTimeMillis()-start));
//
//                }
//            });
//            image.setImageResource(R.drawable.anim_loading);
//            Glide.with(getContext()).
//                    load(data.getCover()).
//                    centerCrop().
//                    placeholder(R.drawable.anim_loading).
//                    error(R.mipmap.app_default_middle).
//                    diskCacheStrategy(DiskCacheStrategy.ALL).
//                    into(image);
//                    into(new SimpleTarget<GlideDrawable>() {
//                @Override
//                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                    image.setImageDrawable(resource);
//                    L.e("glide image load time:"+(System.currentTimeMillis()-start));
//                }
//            });

        }
    }


}
