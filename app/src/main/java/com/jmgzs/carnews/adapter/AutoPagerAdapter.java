package com.jmgzs.carnews.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmgzs.autoviewpager.RecyclingPagerAdapter;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.bean.NewsDataBean;

import java.util.ArrayList;

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

    public AutoPagerAdapter(Context context, ArrayList<NewsDataBean> lists) {
        this.mContext = context;
        this.infos = lists;
        this.size = infos == null || infos.size() == 0 ? 5 : infos.size();
        isInfiniteLoop = false;
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
//        NewsDataBean info = getItem(pos);
//        if (info == null) {
//            ivPic.setImageResource(R.mipmap.new_comer_header_bg);
//        } else {
//            ivPic.setImageURI(Uri.parse(info.getBanner()));
//
//            if (TextUtils.isEmpty(info.getLt_icon())) {
//                ivIcon.setVisibility(View.GONE);
//            } else {
//                ivIcon.setVisibility(View.VISIBLE);
//                ivIcon.setImageURI(Uri.parse(info.getLt_icon()));
//            }
//        }
//
//        ivPic.setTag(R.id.tag_position, pos);
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
}
