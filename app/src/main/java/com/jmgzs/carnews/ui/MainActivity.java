package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvRequestUtil;
import com.jmgsz.lib.adv.bean.AdvRequestBean;
import com.jmgsz.lib.adv.interfaces.IAdvRequestCallback;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.HomeAdapter;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.ui.tab.HomeTabProvider;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;
import com.jmgzs.lib_network.utils.L;
import com.ogaclejapan.smarttablelayout.SmartTabLayout;

public class MainActivity extends BaseActivity {

    private HomeTabProvider tabProvider;
    private ViewPager vPager;
    private int currentPosition = 0;
    private HomeAdapter adapter;
    private RoundedImageView head;

    @Override
    protected int getContent(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        head = getView(R.id.btn_right);
        head.setOnClickListener(this);

        vPager = getView(R.id.main_pager);
        adapter = new HomeAdapter(getSupportFragmentManager(), this);
        vPager.setAdapter(adapter);
        vPager.setOffscreenPageLimit(1);

        tabProvider = new HomeTabProvider(this);
        SmartTabLayout tab = getView(R.id.pager_tab);
        tab.setCustomTabView(tabProvider);
//        tab.setCustomTabView(R.layout.tab_item,R.id.tab_item_text);
        tab.setViewPager(vPager);
        tab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (positionOffset > 0) {
//                    tabProvider.getTabItem(position).setTabAlpha(1 - positionOffset);
//                    tabProvider.getTabItem(position + 1).setTabAlpha(positionOffset);
//                } else {
//                    tabProvider.getTabItem(position).setTabAlpha(1 - positionOffset);
//                }
//                float scale = 0.2f;
//                if (position == 0) {
//                    // move to left
//                    float scaleLeft = 1 - (positionOffset) * scale;
//                    TabItem left = tabProvider.getTabItem(position);
//                    left.setScaleX(scaleLeft);
//                    left.setScaleY(scaleLeft);
//
//                    float scaleRight = 1 - (1 - positionOffset) * scale;
//                    TabItem right = tabProvider.getTabItem(position + 1);
//                    right.setScaleX(scaleRight);
//                    right.setScaleY(scaleRight);
//                }
//                ivTopLeftIcon.setAlpha(1 - positionOffset);
//                ivTopLeftIcon.setTranslationX(-ivTopLeftIcon.getWidth() * 2 * positionOffset);

                L.i(position + "," + positionOffset + "," + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                tabProvider.getTabItem(position).setTabAlpha(1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.headPath != null)
            GlideApp.with(this).asBitmap().centerInside().
                    placeholder(R.mipmap.user_head_default).error(R.mipmap.user_head_default).load(App.headPath).into(head);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_right:
                startActivity(new Intent(this, UserSettingActivity.class));
//                Intent intent = new Intent(this, NewsInfoActivity.class);
//                intent.putExtra("aid", "4");
//                startActivity(intent);
//                String req = "{\"id\":\"ebb7fbcb-01da-4255-8c87-98eedbcd2909\",\"user_agent\":\"Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; R8007 Build/JLS36C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30\",\"app_site_info\":{\"appsite_id\":\"51e71da6-5b46-4d9f-b94f-9ec6a\",\"categories\":[0],\"app_bundle_name\":\"dongfangtoutiao_test\",\"app_name\":\"\"},\"net_type\":2,\"ad_slot_info\":[{\"sid\": 0,\"height\": 220,\"screen_position\": 1,\"loc_id\": \"bf8a85e6-849e-11e6-8c73-a4dcbef43d46\",\"width\": 156,\"ad_num\": 1,\"html_material\": false}],\"id_info\": {\"mac\": \"d8:55:a3:ce:e4:40\",\"idfa\": \"5b7e9e4f42a6635f\"},\"device_info\": {\"orientation\": 2,\"model\": \"MX5\",\"brand\": \"MEIXU\",\"screen_width\": 1080,\"type\": 2,\"screen_height\": 1920},\"user_ip\": \"58.30.22.0\",\"template_id\": [2044,2001],\"channel_id\": 1001}";
//                AdvRequestUtil.requestAdv(this, new Gson().fromJson(req, AdvRequestBean.class), new IAdvRequestCallback() {
//                    @Override
//                    public void onGetAdvSuccess(String html) {
//
//                    }
//
//                    @Override
//                    public void onGetAdvFailure() {
//
//                    }
//                });
                break;
            default:
                break;
        }
    }
}
