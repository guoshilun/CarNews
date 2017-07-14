package com.jmgzs.carnews.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.base.GlideApp;
import com.jmgzs.carnews.bean.UpdateBean;
import com.jmgzs.carnews.bean.UpdateInfo;
import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.network.update.UpdateDownloadListener;
import com.jmgzs.carnews.ui.dialog.BaseDialog;
import com.jmgzs.carnews.ui.dialog.DialogMenu;
import com.jmgzs.carnews.ui.dialog.IMenuItemClickListerer;
import com.jmgzs.carnews.ui.dialog.UpdateDialog;
import com.jmgzs.carnews.ui.view.SettingItemView;
import com.jmgzs.carnews.util.AppUtils;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.FileProvider7;
import com.jmgzs.carnews.util.GetPathFromUri4kitkat;
import com.jmgzs.carnews.util.GlideCacheUtil;
import com.jmgzs.carnews.util.LoaderUtil;
import com.jmgzs.carnews.util.SPBase;
import com.jmgzs.carnews.util.T;
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib.view.roundedimage.RoundedImageView;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.FileUtils;
import com.jmgzs.lib_network.utils.L;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.jmgzs.carnews.util.Const.PhotoCode.CACHE_TAKE_PHOTO;


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
    private String[] textSizeTitle = {"小", "标准", "大"};
    private int type = 1;
    private boolean isStoreListNotNull = false;

    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_user_setting;
    }

    @Override
    protected void initView() {

        TextView title = getView(R.id.titleInfo_tv_title);
        title.setText(R.string.user_setting);
        getView(R.id.titleInfo_img_more).setVisibility(View.INVISIBLE);
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

    @Override
    protected String getUmengKey() {
        return UmengUtil.U_SETTING;
    }


    private void showRightSetting() {
        itemStore.show(false, false, false, true);
        itemWifi.show(true, false, true, false);
        itemTextSize.show(false, true, false, true);
        itemPush.show(true, false, true, false);
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
        itemPush.setTvTips(R.string.setting_push_tips);

        itemStore.setOnClickListener(this);
        itemTextSize.setOnClickListener(this);
        itemCache.setOnClickListener(this);
        itemUpdate.setOnClickListener(this);
        itemUser.setOnClickListener(this);

        itemWifi.setonCheckChangedListener(this);
        itemPush.setonCheckChangedListener(this);

        if (App.headPath != null)
            GlideApp.with(this).asBitmap().fitCenter().error(R.mipmap.user_icon).
                    placeholder(R.mipmap.user_icon).load(App.headPath).into(headImage);

        itemCache.setTextState(GlideCacheUtil.getInstance().getCacheSize(this));
        itemWifi.setChecked(App.isMobile);
        itemPush.setChecked(App.isRecptPush);
        type = SPBase.getInt(Const.SPKey.TEXT_SIZE, 1);
        itemTextSize.setTextState(textSizeTitle[type]);
        isStoreListNotNull = LoaderUtil.get().hasStored(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titleInfo_img_back:
                finish();
                break;

            case R.id.user_head:
                showPhotoMenu();
                UmengUtil.event(this, UmengUtil.U_SETTING_HEAD);
                break;
            case R.id.setting_store:
                UmengUtil.event(this, UmengUtil.U_SETTING_STORE);
                if (isStoreListNotNull)
                    startActivity(new Intent(this, NewsStoreActivity.class));
                else T.toastS("尚未收藏任何新闻");
                break;
            case R.id.setting_update:
                checkUpdate();
                break;
            case R.id.setting_user:
                startActivity(new Intent(this, MianZeActivity.class));
                break;
            case R.id.setting_cache:
                if (!TextUtils.isEmpty(itemCache.getTextState()))
                    showCacheMenu();

                break;
            case R.id.setting_textsize:
                showTextMenu();
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
//        删除文件夹导致头像,广告显示异常
//        FileUtils.deleteFile(FileUtils.getAppBaseFile(this));
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
        menuDialog.setMenuItemTopText(View.VISIBLE, "确定删除所有缓存?");
        menuDialog.setMenuItemMiddle1Text(View.GONE, null);
        menuDialog.setMenuItemMiddle2Text(View.GONE, null);
        menuDialog.setMenuItemBottomText(View.VISIBLE, "确定");
    }

    private void showTextMenu() {
        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                int newType = -1;
                switch (position) {
                    case DialogMenu.MENU_ITEM_TOP:
                        newType = 0;
                        break;
                    case DialogMenu.MENU_ITEM_MIDDLE_1:
                        newType = 1;
                        break;
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        newType = 2;
                        break;
                    default:
                        return;
                }
                if (type != newType) {
                    itemTextSize.setTextState(textSizeTitle[newType]);
                    SPBase.putInt(Const.SPKey.TEXT_SIZE, newType);
                    showAppRestartMenu();
                }
            }
        });
        menuDialog.show();
        menuDialog.setMenuItemTopText(View.VISIBLE, "小");
        menuDialog.setMenuItemMiddle1Text(View.VISIBLE, "标准");
        menuDialog.setMenuItemMiddle2Text(View.GONE, null);
        menuDialog.setMenuItemBottomText(View.VISIBLE, "大");

    }

    private void showAppRestartMenu() {
        DialogMenu menuDialog = new DialogMenu(this);
        menuDialog.setOnMenuItemClickListener(new IMenuItemClickListerer() {

            @Override
            public void onMenuItemClick(int position) {
                switch (position) {
                    case DialogMenu.MENU_ITEM_BOTTOM:
                        restartApp();
                        break;

                    default:
                        break;
                }
            }
        });
        menuDialog.show();
        menuDialog.setMenuItemTopText(View.VISIBLE, "字体设置完成,重启后生效");
        menuDialog.setMenuItemMiddle1Text(View.GONE, null);
        menuDialog.setMenuItemMiddle2Text(View.GONE, null);
        menuDialog.setMenuItemBottomText(View.VISIBLE, "立即重启");
    }

    private void restartApp() {
        Intent in = getPackageManager().getLaunchIntentForPackage(getPackageName());
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }

    private void checkUpdate() {
        RequestUtil.requestByGetAsy(this, Urls.getUpdateUrl(), UpdateBean.class, new IRequestCallBack<UpdateBean>() {
            @Override
            public void onSuccess(String url, UpdateBean data) {
                if (data != null && data.getRsp().getStatus() == 1
                        && data.getData() != null && data.getData().getVersion() > AppUtils.getVersionNum())
                    showUpdateDialog(data.getData());
                else T.toastS("当前版本为最新版!");

            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                T.toastS("服务器异常,请稍后重试。");
            }

            @Override
            public void onCancel(String url) {

            }
        });
    }

    private void showUpdateDialog(final UpdateInfo data) {
        UpdateDialog updateDialog = new UpdateDialog(this);
        updateDialog.show();
        updateDialog.setData(data.getMsg(), data.isForce());
        updateDialog.setOnDialogClickListener(new BaseDialog.OnDialogClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (updateListener == null)
                    updateListener = new UpdateDownloadListener(UserSettingActivity.this);
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

    private UpdateDownloadListener updateListener;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Const.PhotoCode.PHOTO_REQUEST_CAMERA:
                    File f = getCameraFile();
                    if (FileUtils.isFileExist(f.getPath()))
                        crop(FileProvider7.getUriForFile(this, f));
                    else T.toastS(this, "图片不存在");

                    break;
                case Const.PhotoCode.PHOTO_REQUEST_GALLERY:
                    if (data.getData() == null) {
                        T.toastS(this, "图片不存在");
                    } else {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            crop(Uri.parse("file://" + GetPathFromUri4kitkat.getPath(this, data.getData())));
                        } else
                            crop(data.getData());
                    }
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
                    } else {
                        T.toastS(this, "裁剪异常");
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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent, Const.PhotoCode.PHOTO_REQUEST_GALLERY);
    }

    private final static String PHOTO_FILE_NAME = "temp_head.jpg";

    /**
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri imgUri = FileProvider7.getUriForFile(this, getCameraFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, Const.PhotoCode.PHOTO_REQUEST_CAMERA);
    }

    private File getCameraFile() {
        File f = new File(FileUtils.getFilePath(this, CACHE_TAKE_PHOTO), PHOTO_FILE_NAME);
        return f;
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
        Uri imageUri = FileProvider7.getUriForFile(this, cropFile);
        cropPath = imageUri.getPath();
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

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

    private boolean checkUriPermission(Intent intent, Uri imgUri) {
        List resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resInfoList.size() == 0) {
            return false;
        }
        Iterator resInfoIterator = resInfoList.iterator();
        while (resInfoIterator.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo) resInfoIterator.next();
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, imgUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return true;
    }
}
