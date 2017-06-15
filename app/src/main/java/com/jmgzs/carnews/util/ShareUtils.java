package com.jmgzs.carnews.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.jmgzs.carnews.ui.view.ShareBoardView;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**分享
 * Created by Wxl on 2017/6/15.
 */

public class ShareUtils {

    public static void init(Context context){
        UMShareAPI.get(context);

        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }

    private static void shareText(Activity context,PlatformConfig.Platform platform, String text, UMShareListener shareListener){
        new ShareAction(context).withText(text)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    private static void shareImg(Activity context,PlatformConfig.Platform platform, String url, String title, String desc, UMShareListener shareListener){
        UMImage img = new UMImage(context, url);
        img.setTitle(title);
        img.setDescription(desc);
        new ShareAction(context).withMedia(img)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    private static void shareImg(Activity context,PlatformConfig.Platform platform, int picRes, String title, String desc, UMShareListener shareListener){
        UMImage img = new UMImage(context, picRes);
        img.setTitle(title);
        img.setDescription(desc);
        new ShareAction(context).withMedia(img)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    private static void shareUrl(Activity context, PlatformConfig.Platform platform, String url, String title, String desc, int picRes, UMShareListener shareListener){
        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(desc);
        web.setThumb(new UMImage(context, picRes));
        new ShareAction(context).withMedia(web)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    public void shareText(final Activity context, final View parent, final String text, final UMShareListener shareListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareText(context, platform, text, shareListener);
            }
        });
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        board.show(parent, xy[0], xy[1]);
    }

    public void shareImg(final Activity context, final View parent, final String url, final String title, final String desc, final UMShareListener shareListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareImg(context, platform, url, title, desc, shareListener);
            }
        });
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        board.show(parent, xy[0], xy[1]);
    }

    public void shareImg(final Activity context, final View parent, final int picRes, final String title, final String desc, final UMShareListener shareListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareImg(context, platform, picRes, title, desc, shareListener);
            }
        });
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        board.show(parent, xy[0], xy[1]);
    }

    public void shareUrl(final Activity context, final View parent, final String url, final String title, final String desc, final int picRes, final UMShareListener shareListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareUrl(context, platform, url, title, desc, picRes, shareListener);
            }
        });
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        L.e("屏幕中的x:"+xy[0]+"\ty:"+xy[1]);
        board.show(parent, xy[0], xy[1]);
    }

}
