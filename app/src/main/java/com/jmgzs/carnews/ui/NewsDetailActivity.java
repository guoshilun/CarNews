package com.jmgzs.carnews.ui;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.base.GlideApp;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class NewsDetailActivity extends BaseActivity {
    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_news_detail;
    }

    @Override
    protected void initView() {

        ImageView imageView = getView(R.id.image);
        ImageView imageView2 = getView(R.id.image2);
        String url = "http://img1.gtimg.com/news/pics/hv1/135/102/2216/144121545.jpg";
        RequestOptions options = new RequestOptions().override(Target.SIZE_ORIGINAL);
        Glide.with(this).load(url)

                .into(imageView);
        GlideApp.with(this).load(url).centerCrop().apply(options).into(imageView2);
    }
}
