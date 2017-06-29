package com.jmgzs.carnews.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.HomeAdapter;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.bean.UpdateBean;
import com.jmgzs.carnews.bean.UpdateInfo;
import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.network.update.UpdateDownloadListener;
import com.jmgzs.carnews.ui.dialog.BaseDialog;
import com.jmgzs.carnews.ui.dialog.UpdateDialog;
import com.jmgzs.carnews.ui.tab.HomeTabProvider;
import com.jmgzs.carnews.ui.tab.TabItem;
import com.jmgzs.carnews.util.AppUtils;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.L;
import com.ogaclejapan.smarttablelayout.SmartTabLayout;

import static android.R.attr.data;
import static android.R.transition.move;

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
        final SmartTabLayout tab = getView(R.id.pager_tab);
        tab.setCustomTabView(tabProvider);
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
////                    // move to left
//                    float scaleLeft = 1 - (positionOffset) * scale;
//                    TabItem left = tabProvider.getTabItem(position);
//                    left.setScaleX(scaleLeft);
//                    left.setScaleY(scaleLeft);
//                    float scaleRight = 1 - (1 - positionOffset) * scale;
//                    TabItem right = tabProvider.getTabItem(position + 1);
//                    right.setScaleX(scaleRight);
//                    right.setScaleY(scaleRight);
//                }
//                ivTopLeftIcon.setAlpha(1 - positionOffset);
//                ivTopLeftIcon.setTranslationX(-ivTopLeftIcon.getWidth() * 2 * positionOffset);

                L.i(position + "," + positionOffset + "," + positionOffsetPixels);
//                changeTextColor(position);
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
//                changeTextColor(position);
//                for (int i = 0, j = adapter.getCount(); i < j; i++) {
//                    if (i == position) {
//                        ( (TabItem)tab.getTabAt(position)).setTabAlpha(1);
//                    }else
//                        ( (TabItem)tab.getTabAt(i)).setTabAlpha(0);
//                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        checkUpdate();
        showUpdateDialog(new UpdateInfo());

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
                break;
            default:
                break;
        }
    }


    private UpdateDownloadListener updateListener;

    private void checkUpdate() {
        RequestUtil.requestByGetAsy(this, Urls.getUpdateUrl(), UpdateBean.class, new IRequestCallBack<UpdateBean>() {
            @Override
            public void onSuccess(String url, UpdateBean data) {
                if (data != null && data.getRsp().getStatus() == 1)
                    showUpdateDialog(data.getData());

            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {

            }

            @Override
            public void onCancel(String url) {

            }
        });
    }

    private void showUpdateDialog(final UpdateInfo data) {
        if (data == null) return;
        UpdateDialog updateDialog = new UpdateDialog(this);
        updateDialog.show();
        updateDialog.setData(data.getMsg(), data.isForce());
        updateDialog.setOnDialogClickListener(new BaseDialog.OnDialogClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (updateListener == null)
                    updateListener = new UpdateDownloadListener(MainActivity.this);
                updateListener.onDownloadStart(data.getUrl(),
                        AppUtils.getAppName() + "v" + AppUtils.getVersionName() + "." + data.getVersion() + "更新",
                        data.getMsg());

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (updateListener != null) {
            updateListener.destroy();

        }
        super.onDestroy();
    }
}
