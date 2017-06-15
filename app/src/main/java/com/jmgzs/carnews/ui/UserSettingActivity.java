package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.ui.dialog.DialogMenu;
import com.jmgzs.carnews.ui.dialog.IMenuItemClickListerer;
import com.jmgzs.carnews.ui.view.SettingItemView;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.SPBase;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;


public class UserSettingActivity extends BaseActivity implements SettingItemView.OnCheckChangedListener {


    private SettingItemView itemStore;

    private SettingItemView itemWifi;
    private SettingItemView itemTextSize;
    private SettingItemView itemPush;
    private SettingItemView itemCache;
    private SettingItemView itemUpdate;
    private SettingItemView itemUser;


    private RoundedImageView headImage;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_user_setting;
    }

    @Override
    protected void initView() {
        TextView title = getView(R.id.titleInfo_tv_title);
        title.setText(R.string.user_setting);
        itemStore = getView(R.id.setting_store);
        itemWifi = getView(R.id.setting_wifi);
        itemTextSize = getView(R.id.setting_textsize);
        itemPush = getView(R.id.setting_push);
        itemCache = getView(R.id.setting_cache);
        itemUpdate = getView(R.id.setting_update);
        itemUser = getView(R.id.setting_user);

        getView(R.id.titleInfo_img_back).setOnClickListener(this);
        headImage = getView(R.id.user_head);
        headImage.setOnClickListener(this);

        showRightSetting();


    }

    private void showRightSetting() {
        itemStore.show(false, false, false, true);
        itemWifi.show(true, false, true, false);
        itemTextSize.show(false, true, false, true);
        itemPush.show(false, false, true, false);
        itemCache.show(false, true, false, true);
        itemUpdate.show(false, false, false, true);
        itemUser.show(false, false, false, true);

        itemStore.setTextTitle(R.string.setting_store);
        itemWifi.setTextTitle(R.string.setting_wifi);
        itemTextSize.setTextTitle(R.string.setting_textsize);
        itemPush.setTextTitle(R.string.setting_push);
        itemCache.setTextTitle(R.string.setting_cache);
        itemUpdate.setTextTitle(R.string.setting_update);
        itemUser.setTextTitle(R.string.setting_user);

        itemStore.setOnClickListener(this);
        itemTextSize.setOnClickListener(this);
        itemCache.setOnClickListener(this);
        itemUpdate.setOnClickListener(this);
        itemUser.setOnClickListener(this);

        itemWifi.setonCheckChangedListener(this);
        itemPush.setonCheckChangedListener(this);

        itemCache.setTextState("0.8MB");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleInfo_img_back:
                finish();
                break;

            case R.id.user_head:
                showMenu();
                break;
            case R.id.setting_store:
                startActivity(new Intent(this, NewsStoreActivity.class));
                break;
            case R.id.setting_push:

                break;
            case R.id.setting_user:

                break;
            case R.id.setting_cache:

                break;
            case R.id.setting_textsize:

                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckChangedListener(View compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.setting_wifi:
                SPBase.putBoolean(Const.SPKey.WIFI, b);
                break;
            case R.id.setting_push:
                SPBase.putBoolean(Const.SPKey.PUSH, b);
                break;
            default:
                break;
        }
    }

    private void showMenu() {
        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case DialogMenu.MENU_ITEM_TOP:
                        break;
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        break;

                    default:
                        break;
                }
            }
        });
        menuDialog.show();
    }
}
