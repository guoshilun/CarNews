package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.ui.dialog.DialogMenu;
import com.jmgzs.carnews.ui.dialog.IMenuItemClickListerer;
import com.jmgzs.carnews.ui.view.SettingItemView;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.GlideCacheUtil;
import com.jmgzs.carnews.util.SPBase;
import com.jmgzs.carnews.util.T;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.jmgzs.carnews.util.Const.PhotoCode.CACHE_TAKE_PHOTO;
import static com.taobao.accs.ACCSManager.mContext;


public class UserSettingActivity extends BaseActivity implements SettingItemView.OnCheckChangedListener {


    private SettingItemView itemStore;

    private SettingItemView itemWifi;
    private SettingItemView itemTextSize;
    private SettingItemView itemPush;
    private SettingItemView itemCache;
    private SettingItemView itemUpdate;
    private SettingItemView itemUser;


    private RoundedImageView headImage;
    private String cropPath = null;

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

        GlideApp.with(this).asBitmap().fitCenter().error(R.mipmap.user_icon).
                placeholder(R.mipmap.user_icon).load(App.headPath).into(headImage);

        itemCache.setTextState(GlideCacheUtil.getInstance().getCacheSize(this));
//        itemCache.setTextState("0.8MB");
        itemWifi.setChecked(App.isMobile);
        itemPush.setChecked(App.isRecptPush);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleInfo_img_back:
                finish();
                break;

            case R.id.user_head:
                showPhotoMenu();
                break;
            case R.id.setting_store:
                startActivity(new Intent(this, NewsStoreActivity.class));
                break;
            case R.id.setting_push:

                break;
            case R.id.setting_user:
                startActivity(new Intent(this, MianZeActivity.class));
                break;
            case R.id.setting_cache:
                if (!TextUtils.isEmpty(itemCache.getTextState()))
                    showCacheMenu();
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
                if (b != App.isMobile)
                    SPBase.putBoolean(Const.SPKey.WIFI, b);
                break;
            case R.id.setting_push:
                if (b != App.isRecptPush)
                    SPBase.putBoolean(Const.SPKey.PUSH, b);
                break;
            default:
                break;
        }
    }

    private void clearGlideCache() {
        GlideCacheUtil.getInstance().clearImageDiskCache(this);
        itemCache.setTextState("");
    }

    private void showPhotoMenu() {
        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case DialogMenu.MENU_ITEM_MIDDLE_2:
                        camera();
                        break;
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        gallery();
                        break;

                    default:
                        break;
                }
            }
        });
        menuDialog.show();
    }

    private void showCacheMenu() {
        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        clearGlideCache();
                        break;

                    default:
                        break;
                }
            }
        });
        menuDialog.show();
        menuDialog.setMenuItemTopText(View.VISIBLE, "删除缓存?");
        menuDialog.setMenuItemMiddle1Text(View.GONE, null);
        menuDialog.setMenuItemMiddle2Text(View.GONE, null);
        menuDialog.setMenuItemBottomText(View.VISIBLE, "确定");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.PhotoCode.PHOTO_REQUEST_CAMERA:
                    File f = getCameraFile();
                    if (FileUtils.isFileExist(f.getPath()))
                        crop(Uri.fromFile(f));
                    else T.toastS(this, "图片路径异常");

                    break;
                case Const.PhotoCode.PHOTO_REQUEST_GALLERY:
                    if (data.getData() == null) {
                        T.toastS(this, "图片路径异常");
                    } else
                        crop(data.getData());
                    break;
                case Const.PhotoCode.PHOTO_REQUEST_CUT:
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null && uri.getPath() != null) {
                            cropPath = uri.getPath();
                        }
                    }
                    if (null != cropPath && FileUtils.isFileExist(cropPath)) {
                        GlideApp.with(this).asBitmap().fitCenter().error(R.mipmap.user_icon).
                                placeholder(R.mipmap.user_icon).load(cropPath).into(headImage);
                        SPBase.putString(Const.SPKey.HEAD_PATH, cropPath);
                        T.toastS("头像更新成功!");
                    } else{
                        T.toastS(this, "裁剪异常" + cropPath);
                        FileUtils.deleteFile(cropPath);
                    }
                    L.e(getClass().getSimpleName(), cropPath);

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Const.PhotoCode.PHOTO_REQUEST_GALLERY);
    }

    private final static String PHOTO_FILE_NAME = "temp_head.jpg";

    /**
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
        startActivityForResult(intent, Const.PhotoCode.PHOTO_REQUEST_CAMERA);
    }

    private File getCameraFile() {
        return new File(FileUtils.getFilePath(this, CACHE_TAKE_PHOTO), PHOTO_FILE_NAME);
    }

    /**
     * 剪切图片
     *
     * @param uri
     * @function:
     * @author:zjy
     * @date:
     */
    private void crop(Uri uri) {
        // 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA);
        String address = sDateFormat.format(new java.util.Date());
        File cropFile = new File(FileUtils.getFilePath(this, CACHE_TAKE_PHOTO), address + ".JPEG");
        cropPath = cropFile.getPath();
        Uri imageUri = Uri.fromFile(cropFile);
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        // 输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // 图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);// 取消人脸识别
        intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
        startActivityForResult(intent, Const.PhotoCode.PHOTO_REQUEST_CUT);
    }
}
