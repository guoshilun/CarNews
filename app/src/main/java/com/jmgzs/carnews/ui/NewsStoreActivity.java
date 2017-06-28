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
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.db.DBHelper;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.js.JsBridge;
import com.jmgzs.carnews.ui.dialog.AdvDialog;
import com.jmgzs.carnews.ui.dialog.DialogMenu;
import com.jmgzs.carnews.ui.dialog.IMenuItemClickListerer;
import com.jmgzs.carnews.util.LoaderUtil;
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
    private JsBridge js;
    private AdvDialog mAdvDialog;

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

        requestAdv();
    }

    private void requestAdv(){
        AdSlotType slotType = AdSlotType.getRandomInsertType();
        if (slotType == null){
            return;
        }
        js = new JsBridge(this, new JsBridge.IJsCallback() {
            @Override
            public void close() {
                if (mAdvDialog != null){
                    mAdvDialog.dismiss();
                }
            }

            @Override
            public void loadFinish() {
            }
        });
//                String req = "{\"id\":\"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"user_agent\":\"Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30\",\"app_site_info\":{\"appsite_id\":\"51e71da6-5b46-4d9f-b94f-9ec6a\",\"categories\":[0],\"app_bundle_name\":\"dongfangtoutiao_test\",\"app_name\":\"\"},\"net_type\":2,\"ad_slot_info\":[{\"sid\": 0,\"height\": 220,\"screen_position\": 1,\"loc_id\": \"bf8a85e6-849e-11e6-8c73-a4dcbef43d46\",\"width\": 156,\"ad_num\": 1,\"html_material\": false}],\"id_info\": {\"mac\": \"d8:55:a3:ce:e4:40\",\"idfa\": \"5b7e9e4f42a6635f\"},\"device_info\": {\"orientation\": 2,\"model\": \"MX5\",\"brand\": \"MEIXU\",\"screen_width\": 1080,\"type\": 2,\"screen_height\": 1920},\"user_ip\": \"58.30.22.0\",\"template_id\": [2044],\"channel_id\": 1001}";
        AdvRequestBean req = AdvRequestUtil.getAdvRequest(NewsStoreActivity.this, slotType);
        L.e("广告请求："+new Gson().toJson(req));
        AdvRequestUtil.requestAdv(NewsStoreActivity.this, req, new IAdvRequestCallback() {
            @Override
            public void onGetAdvSuccess(String html, int width, int height) {
                L.e("广告请求成功");
                L.e("adv Html:"+html);
                showAdv(html, width, height);
            }

            @Override
            public void onGetAdvFailure() {
                L.e("广告请求失败");
                showAdv("<p>广告请求失败</p>", js.getPageWidth(), 60);

            }
        });
    }

    private void showAdv(final String html, int width, int height){
        if (mAdvDialog == null){
            mAdvDialog = new AdvDialog(this);
            mAdvDialog.setWidthHeight(width, height);
            mAdvDialog.setListener(new AdvDialog.IOnAdvLoadListener() {
                @Override
                public void onAdvLoad(WebView wv) {
                    wv.addJavascriptInterface(js, "carnews");
                    wv.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
                }
            });
        }
        mAdvDialog.show();
    }
    private void selectData() {
//        DBHelper helper = DBHelper.getInstance(this);
        List<NewsDataBean> all = LoaderUtil.get().loadCache(this);
        if (all != null)
            adapter.updateData(all);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        L.e("click item " + i);
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
                        }else {
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
