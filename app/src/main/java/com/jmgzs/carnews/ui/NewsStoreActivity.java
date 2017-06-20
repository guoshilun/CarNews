package com.jmgzs.carnews.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.StoreAdapter;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.db.DBHelper;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib.swipelistview.SwipeMenu;
import com.jmgzs.lib.swipelistview.SwipeMenuCreator;
import com.jmgzs.lib.swipelistview.SwipeMenuItem;
import com.jmgzs.lib.swipelistview.SwipeMenuListView;

import java.util.ArrayList;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class NewsStoreActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private SwipeMenuListView listView;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_news_store;
    }

    @Override
    protected void initView() {
        getView(R.id.titleInfo_img_back).setOnClickListener(this);
        TextView title = getView(R.id.titleInfo_tv_title);
        title.setText(R.string.setting_store);
        listView = getView(R.id.swipe_list);

        StoreAdapter adapter = new StoreAdapter(this, null);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item = new SwipeMenuItem(NewsStoreActivity.this);
                item.setBackground(R.color.colorTransparent);
                item.setWidth(DensityUtils.dip2px(NewsStoreActivity.this, 60));
                item.setIcon(R.mipmap.delete_icon);
                menu.addMenuItem(item);

            }
        };
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                L.e("index=" + index + ",pos=" + position);
                showDeleteDialog(position);
            }
        });
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
    }

    private void selectData() {

        DBHelper helper = DBHelper.getInstance(this);
        ArrayList<NewsDataBean> all = helper.queryNews();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        L.e("click item " + i);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private void showDeleteDialog(int pos) {

    }
}
