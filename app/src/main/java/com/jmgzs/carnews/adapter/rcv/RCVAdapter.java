package com.jmgzs.carnews.adapter.rcv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmgsz.lib.adv.interfaces.IAdvStatusCallback;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.adapter.rcvbase.RCVBaseAdapter;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.network.NetWorkReciver;
import com.jmgzs.carnews.util.TimeUtils;
import com.jmgzs.lib_network.utils.L;

import java.util.List;


/**
 * Created by XJ on 2016/9/23.
 */
public class RCVAdapter extends RCVBaseAdapter<NewsDataBean, BaseHolder<NewsDataBean>> {

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
            return new RCVImageHolder(parent, R.layout.item_view_image1, imageW, imageH);
        else if(viewType == 1){
            return new RCVHolder(parent, R.layout.item_view_image3, imageW, imageH);
        }else {
            return new RCVAdvHolder(parent, R.layout.item_view_adv, new IAdvStatusCallback() {
                @Override
                public void close(int id) {
//                    removeItem(0);
                }

            });
        }
    }

    @Override
    public int getViewType(int position) {
        return data.get(position-1).getItemType().getValue();
    }
}
