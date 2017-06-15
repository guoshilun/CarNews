package com.jmgzs.carnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jmgzs.carnews.R;
import com.umeng.socialize.PlatformConfig;

import java.util.List;

/**分享面板九宫格适配器
 * Created by Wxl on 2017/6/15.
 */

public class ShareGridAdapter extends BaseAdapter {

    private Context context;
    private List<PlatformConfig.Platform> data;

    public ShareGridAdapter(Context context, List<PlatformConfig.Platform> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public PlatformConfig.Platform getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data == null ? 0 : data.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view_share_platform, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        switch (data.get(position).getName()){
            case WEIXIN:
                holder.img.setImageResource(R.mipmap.wx_share);
                break;
            case WEIXIN_CIRCLE:
                holder.img.setImageResource(R.mipmap.wx_circle_share);
                break;
            case QQ:
                holder.img.setImageResource(R.mipmap.qq_share);
                break;
            case QZONE:
                holder.img.setImageResource(R.mipmap.qq_zone_share);
                break;
        }

        return convertView;
    }

    private static class ViewHolder {
        public ImageView img;

        public ViewHolder(View convertView){
            img = (ImageView) convertView.findViewById(R.id.itemViewSharePlatform_img_platform);
        }
    }

}
