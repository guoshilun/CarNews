package com.jmgzs.carnews.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.network.NetWorkReciver;
import com.jmgzs.carnews.util.TimeUtils;
import com.jmgzs.lib_network.utils.L;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.author;
import static android.R.attr.data;


/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class StoreAdapter extends BaseAdapter {

    private final GlideRequest<Drawable> request;
    private List<NewsDataBean> dataList;
    private Context ct;

    private final static int TYPE0 = 0;//无图
    private final static int TYPE1 = 1;//1图
    private final static int TYPE2 = 2;//3图

    private volatile int imageW = 90;
    private volatile int imageH = 70;

    public StoreAdapter(Context ct, List<NewsDataBean> dataList) {
        this.ct = ct;
        this.dataList = dataList;

        imageW = (DensityUtils.SCREEN_WIDTH_PIXELS - DensityUtils.dip2px(ct, (5 * 4))) / 3;
        imageH = imageW - DensityUtils.dip2px(ct, 20);

        request = GlideApp.with(ct).asDrawable().centerCrop().error(R.mipmap.app_default_middle);

        L.e("adapter w=" + imageW + ",h=" + imageH);

    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public NewsDataBean getItem(int i) {
        return dataList.get(i);
//        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        ISetData h;
        if (view == null)
            if (type == TYPE0 || type == TYPE2) {
                view = View.inflate(ct, R.layout.item_view_image3, null);
                h = new Holder3(view, imageW, imageH);
                view.setTag(h);
            } else {
                view = View.inflate(ct, R.layout.item_view_image1, null);
                h = new Holder(view, imageW, imageH);
                view.setTag(h);
            }
        else
            h = (ISetData) view.getTag();
        h.setData(getItem(i), request);
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        List<String> images = getItem(position).getImg_list();
        return images == null || images.size() == 0 ? TYPE0 : (images.size() == 3 ? TYPE2 : TYPE1);
    }

    private interface ISetData {
        void setData(NewsDataBean b, GlideRequest request);
    }

    private static class Holder implements ISetData {

        private ImageView image;
        private TextView title;
        private TextView source;
        private TextView publishTime;
        private LinearLayout cotentLayout;

        public Holder(View v, int imageW, int imageH) {
            title = (TextView) v.findViewById(R.id.item_text);
            source = (TextView) v.findViewById(R.id.item_author);
            publishTime = (TextView) v.findViewById(R.id.item_time);
            image = (ImageView) v.findViewById(R.id.item_image);
            cotentLayout = (LinearLayout) v.findViewById(R.id.item_content_layout);
            cotentLayout.getLayoutParams().height = imageH;
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageW, imageH);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
            params.width = imageW;
            params.height = imageH;
//            L.e("w=" + imageW + ",h=" + imageH);
            image.setLayoutParams(params);
        }

        @Override
        public void setData(NewsDataBean b, GlideRequest request) {
            if (b == null) {
                return;
            }
            title.setText(b.getTitle());
            source.setText(b.getPublish_source());
            publishTime.setText(TimeUtils.getTimeFromDateString(b.getPublish_time()));
            if (b.getImg_list().size() == 0) {
                image.setImageResource(R.mipmap.app_default_middle);
            } else
                request.load(b.getImg_list().get(0)).into(image);

        }
    }

    //三图片无图共用
    private static class Holder3 implements ISetData {

        private ImageView image;
        private ImageView image2;
        private ImageView image3;
        private TextView title;
        private TextView source;
        private TextView publishTime;
        private LinearLayout imagesLayout;

        public Holder3(View v, int imageW, int imageH) {
            title = (TextView) v.findViewById(R.id.item_text);
            source = (TextView) v.findViewById(R.id.item_author);
            publishTime = (TextView) v.findViewById(R.id.item_time);
            image = (ImageView) v.findViewById(R.id.item_image);
            image2 = (ImageView) v.findViewById(R.id.item_image2);
            image3 = (ImageView) v.findViewById(R.id.item_image3);
            imagesLayout = (LinearLayout) v.findViewById(R.id.item_images_layout);

            imagesLayout.getLayoutParams().height = imageH;
        }

        @Override
        public void setData(NewsDataBean data, GlideRequest request) {
            if (data == null) {
                return;
            }
            title.setText(data.getTitle());
            source.setText(data.getPublish_source());
            publishTime.setText(TimeUtils.getTimeFromDateString(data.getPublish_time()));
            if (data.getImg_list() == null || data.getImg_list().size() < 3) {
                imagesLayout.setVisibility(View.GONE);
            } else {
                imagesLayout.setVisibility(View.VISIBLE);
                request.load(data.getImg_list().get(0)).into(image);
                request.load(data.getImg_list().get(1)).into(image2);
                request.load(data.getImg_list().get(2)).into(image3);
            }

        }
    }

    public void updateData(List<NewsDataBean> data) {
        if (dataList == null) dataList = new ArrayList<>();
        else dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        NewsDataBean d = dataList.remove(pos);
        if (d != null) {
            notifyDataSetChanged();
        }
    }


}
