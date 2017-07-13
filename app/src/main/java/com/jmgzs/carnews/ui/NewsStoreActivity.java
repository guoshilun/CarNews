package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.StoreAdapter;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.db.DBHelper;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.dialog.AdvDialog;
import com.jmgzs.carnews.ui.dialog.DialogMenu;
import com.jmgzs.carnews.ui.dialog.IMenuItemClickListerer;
import com.jmgzs.carnews.util.InsertAdvUtil;
import com.jmgzs.carnews.util.LoaderUtil;
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;
import com.jmgzs.lib.swipelistview.SwipeMenu;
import com.jmgzs.lib.swipelistview.SwipeMenuCreator;
import com.jmgzs.lib.swipelistview.SwipeMenuItem;
import com.jmgzs.lib.swipelistview.SwipeMenuListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class NewsStoreActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private SwipeMenuListView listView;
    private StoreAdapter adapter;
    private InsertAdvUtil insertAdvReq;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_news_store;
    }

    @Override
    protected void initView() {
        getView(R.id.titleInfo_img_back).setOnClickListener(this);
        getView(R.id.titleInfo_img_more).setOnClickListener(this);
        TextView title = getView(R.id.titleInfo_tv_title);
        title.setText(R.string.setting_store);
        listView = getView(R.id.swipe_list);

        adapter = new StoreAdapter(this, LoaderUtil.get().loadCache(this));
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

        insertAdvReq = new InsertAdvUtil(this);
        insertAdvReq.requestAdv();
    }

    @Override
    protected String getUmengKey() {
        return UmengUtil.U_STORELIST;
    }


//    private void selectData() {
//        List<NewsDataBean> all = LoaderUtil.get().loadCache(this);
//        if (all != null)
//            adapter.updateData(all);
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0 || i > adapterView.getCount()) return;
        Intent in = new Intent(this, NewsInfoActivity.class);
        in.putExtra("aid", ((NewsDataBean) adapterView.getItemAtPosition(i)).getAid());
        startActivity(in);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleInfo_img_back:
                finish();
                break;
            case R.id.titleInfo_img_more:
                showDeleteDialog(-1);
                break;
            default:
                break;
        }
    }

    private void showDeleteDialog(final int pos) {

        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        if (pos != -1) {
                            LoaderUtil.get().deleteNews(NewsStoreActivity.this, adapter.getItem(pos).getAid() + "");
                            adapter.removeItem(pos);
                        } else {
                            LoaderUtil.get().deleteAllNews(NewsStoreActivity.this);
                            adapter.clear();
                        }
                        break;

                    default:
                        break;
                }
            }
        });
        menuDialog.show();
        if (pos == -1) {
            menuDialog.setMenuItemTopText(View.VISIBLE, "清空收藏列表?");
        } else
            menuDialog.setMenuItemTopText(View.VISIBLE, "删除该条收藏?");
        menuDialog.setMenuItemMiddle1Text(View.GONE, null);
        menuDialog.setMenuItemMiddle2Text(View.GONE, null);
        menuDialog.setMenuItemBottomText(View.VISIBLE, "确定");
    }
}
