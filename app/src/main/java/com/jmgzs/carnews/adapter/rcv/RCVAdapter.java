package com.jmgzs.carnews.adapter.rcv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.Key;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.adapter.rcvbase.RCVBaseAdapter;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.util.L;

import java.util.List;


/**
 * Created by XJ on 2016/9/23.
 */
public class RCVAdapter extends RCVBaseAdapter<NewsDataBean, BaseHolder<NewsDataBean>> {

    private static int[] actualSize;

    private static GlideRequest<Drawable> request;


    public RCVAdapter(Context ct, List<NewsDataBean> data, OnRCVItemClickListener.OnItemClickListener itemClickListener) {
        super(data, itemClickListener);
        request = GlideApp.with(ct).asDrawable().centerCrop();

    }

    public RCVAdapter(List<NewsDataBean> data) {
        super(data);
    }


    @Override
    public BaseHolder<NewsDataBean> onCreateVH(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new Holder(parent, R.layout.item_view_image1);
        else return new RCVHolder(parent, R.layout.item_view_image3);
    }

    @Override
    public int getViewType(int position) {
//        return super.getItemViewType(position - getHeadersCount());
//        return data!=null&&data.get(position).getPhoto().size() == 1 ? 0 : 1;
        return position%3==0?0:1;
    }

    public static class Holder extends BaseHolder<NewsDataBean> {
//        implements ListPreloader.PreloadModelProvider<Photo>, ListPreloader.PreloadSizeProvider<Photo> {

        private ImageView image;
        private TextView title;

        private int[] actualSize;



        public Holder(ViewGroup parent, @LayoutRes int layout) {
            super(parent, layout);
            image = getView(R.id.item_image);
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
//            request = GlideApp.with(getContext()).asDrawable().centerCrop();

        }

        @Override
        public void setData(NewsDataBean data) {
            if (data == null) return;
            L.e(data.toString());
            title.setText(data.getTitle());
            request.
                    load(data.getPhoto().get(0).getUrl()).
//                placeholder(R.mipmap.ic_launcher).
//                error(R.mipmap.ic_launcher).
//                signature(signature).
//                diskCacheStrategy(DiskCacheStrategy.ALL).
        into(image);

        }
    }

}
