package com.jmgzs.carnews.adapter.rcv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.adapter.rcvbase.RCVBaseAdapter;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.network.NetWorkReciver;
import com.jmgzs.carnews.util.TimeUtils;
import com.jmgzs.lib_network.utils.L;

import java.util.List;


/**
 * Created by XJ on 2016/9/23.
 */
public class RCVAdapter extends RCVBaseAdapter<NewsDataBean, BaseHolder<NewsDataBean>> {

//    private static int[] actualSize;

    private static GlideRequest<Drawable> request;

    private int imageW = 90;
    private int imageH = 90;

    public RCVAdapter(Context ct, List<NewsDataBean> data, OnRCVItemClickListener.OnItemClickListener itemClickListener) {
        super(data, itemClickListener);
        request = GlideApp.with(ct).asDrawable().centerCrop();
        imageW = (DensityUtils.SCREEN_WIDTH_PIXELS - DensityUtils.dip2px(ct, (5 * 4 + 8 * 2))) / 3;
        imageH = imageW - DensityUtils.dip2px(ct, 20);

    }

    public RCVAdapter(List<NewsDataBean> data) {
        super(data);
    }


    @Override
    public BaseHolder<NewsDataBean> onCreateVH(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new Holder(parent, R.layout.item_view_image1, imageW, imageH);
        else {
            return new RCVHolder(parent, R.layout.item_view_image3, imageW, imageH);
        }
    }

    @Override
    public int getViewType(int position) {
//        L.e(position+"- adapter view type position");
        return data != null && data.get(position - 1).getImg_list().size() < 3 ? 0 : 1;
    }

    public static class Holder extends BaseHolder<NewsDataBean> {

        private ImageView image;
        private TextView title;
        private TextView author;
        private TextView time;

        public Holder(ViewGroup parent, @LayoutRes int layout, int w, int h) {
            super(parent, layout);
            image = getView(R.id.item_image);
            title = getView(R.id.item_text);
            author = getView(R.id.item_author);
            time = getView(R.id.item_time);
            LinearLayout cotentLayout = getView(R.id.item_content_layout);
            cotentLayout.getLayoutParams().height = h;

            image.getLayoutParams().width = w;
            image.getLayoutParams().height = h;
        }

        @Override
        public void setData(NewsDataBean data) {
            if (data == null) return;
            title.setText(data.getTitle());
            author.setText(data.getPublish_source());
            time.setText(TimeUtils.getTimeFromDateString(data.getPublish_time()));
            if ((App.isMobile && NetWorkReciver.isMobile(getContext())) || data.getImg_list().size() == 0) {
                image.setImageResource(R.mipmap.app_default_middle);
            } else
                request.load(data.getImg_list().get(0)).into(image);

        }
    }

}
