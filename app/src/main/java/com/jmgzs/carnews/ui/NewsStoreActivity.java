package com.jmgzs.carnews.ui;

import android.content.Intent;
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
import com.jmgzs.carnews.ui.dialog.DialogMenu;
import com.jmgzs.carnews.ui.dialog.IMenuItemClickListerer;
import com.jmgzs.carnews.util.LoaderUtil;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib.swipelistview.SwipeMenu;
import com.jmgzs.lib.swipelistview.SwipeMenuCreator;
import com.jmgzs.lib.swipelistview.SwipeMenuItem;
import com.jmgzs.lib.swipelistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class NewsStoreActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private SwipeMenuListView listView;
private StoreAdapter adapter ;
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

          adapter = new StoreAdapter(this, null);
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
        selectData();
    }

    private void selectData() {
//        DBHelper helper = DBHelper.getInstance(this);
        List<NewsDataBean> all = LoaderUtil.get().loadCache(this);
        if (all!=null)
        adapter.updateData(all);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        L.e("click item " + i);
        if (i <0||i>adapterView.getCount()) return;
        Intent in =new Intent(this ,NewsInfoActivity.class);
        in.putExtra("aid",((NewsDataBean)adapterView.getItemAtPosition(i)).getAid());
        startActivity(in);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private void showDeleteDialog(final int pos) {

        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        LoaderUtil.get().deleteNews(NewsStoreActivity.this,adapter.getItem(pos).getAid()+"");
                        break;

                    default:
                        break;
                }
            }
        });
        menuDialog.show();
        menuDialog.setMenuItemTopText(View.VISIBLE, "删除该条收藏?");
        menuDialog.setMenuItemMiddle1Text(View.GONE, null);
        menuDialog.setMenuItemMiddle2Text(View.GONE, null);
        menuDialog.setMenuItemBottomText(View.VISIBLE, "确定");
    }
}
