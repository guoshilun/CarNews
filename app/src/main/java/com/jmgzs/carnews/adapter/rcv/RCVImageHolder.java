package com.jmgzs.carnews.adapter.rcv;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.rcvbase.BaseHolder;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.base.GlideRequest;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.network.NetWorkReciver;
import com.jmgzs.carnews.util.TimeUtils;

/**
 * Created by mac on 17/6/30.
 * Description: 单图item
 */

public class RCVImageHolder extends BaseHolder<NewsDataBean> {

    private ImageView image;
    private TextView title;
    private TextView author;
    private TextView time;
    private GlideRequest request;

    public RCVImageHolder(ViewGroup parent, @LayoutRes int layout, int w, int h) {
        super(parent, layout);
        image = getView(R.id.item_image);
        title = getView(R.id.item_text);
        author = getView(R.id.item_author);
        time = getView(R.id.item_time);
        LinearLayout cotentLayout = getView(R.id.item_content_layout);
        cotentLayout.getLayoutParams().height = h;

        image.getLayoutParams().width = w;
        image.getLayoutParams().height = h;
        request = GlideApp.with(getContext()).asDrawable().centerCrop().error(R.mipmap.app_default_middle)
                .placeholder(R.mipmap.app_default_middle);

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