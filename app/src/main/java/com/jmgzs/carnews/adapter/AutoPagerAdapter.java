package com.jmgzs.carnews.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmgzs.autoviewpager.RecyclingPagerAdapter;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.network.NetWorkReciver;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.SPBase;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class AutoPagerAdapter extends RecyclingPagerAdapter {
    private Context mContext;
    private ArrayList<NewsDataBean> infos;
    private View.OnClickListener bannerListener = null;

    private int size;
    private boolean isInfiniteLoop;
    private GlideRequest<Drawable> request;

    public AutoPagerAdapter(Context context, ArrayList<NewsDataBean> lists) {
        this.mContext = context;
        this.infos = lists;
        this.size = (infos == null || infos.size() == 0 ? 1 : infos.size());
        isInfiniteLoop = false;
        request = GlideApp.with(context).asDrawable().error(R.mipmap.app_default_middle).centerCrop();
    }

    @Override
    public int getCount() {
        return isInfiniteLoop ? Integer.MAX_VALUE : size;
    }

    public int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    public NewsDataBean getItem(int position) {
        if (position >= 0 && infos != null && position < infos.size()) {
            return infos.get(position);
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.auto_pager_item_view, null);
        }

        ImageView ivPic = (ImageView) convertView.findViewById(R.id.item_image);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.item_text);

        int pos = getPosition(position);
        NewsDataBean info = getItem(pos);
        if (info == null) {
            ivPic.setImageResource(R.mipmap.app_default_middle);
            tvTitle.setText("");
        } else {
            tvTitle.setText(info.getTitle());
            if ((App.isMobile && NetWorkReciver.isMobile(mContext)) ||info.getImg_list().size()==0) {
                ivPic.setImageResource(R.mipmap.app_default_middle);
            } else
                request.load(info.getImg_list().get(0)).into(ivPic);
        }
        ivPic.setOnClickListener(bannerListener);

        return convertView;
    }

    public void setBannerClickListener(View.OnClickListener listener) {
        this.bannerListener = listener;
    }

    public boolean isInfiniteLoop() {
        return this.isInfiniteLoop;
    }

    public void setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void updateData(ArrayList<NewsDataBean> dataBeen) {
        infos.clear();
        infos.addAll(dataBeen);
        notifyDataSetChanged();
    }

    public ArrayList<NewsDataBean> getData(){
        return infos;
    }
}
