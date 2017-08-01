package com.jmgzs.carnews.util;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.view.View;

import com.jmgzs.carnews.ui.view.ShareBoardView;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.common.QueuedWork;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.jmgzs.lib_network.utils.L;

import java.util.WeakHashMap;

/**分享
 * Created by Wxl on 2017/6/15.
 */

public class ShareUtils {

    private static WeakHashMap<Context, ShareBoardView> showDialog = new WeakHashMap<>();

    public static void init(Context context){
        PlatformConfig.setWeixin("wx9b5cb75750f4ed62", "346b090018f9bcad83ad192b05ae2761");
        PlatformConfig.setQQZone("101404695", "73a1de5a0018604073da2847a08fda06");
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        UMShareAPI.get(context);
    }

    private static void shareText(Activity context,PlatformConfig.Platform platform, CharSequence text, UMShareListener shareListener){
        new ShareAction(context).withText(new SpannableString(text).toString())
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    private static void shareImg(Activity context,PlatformConfig.Platform platform, String url, CharSequence title, CharSequence desc, UMShareListener shareListener){
        UMImage img = new UMImage(context, url);
        img.setTitle(new SpannableString(title).toString());
        img.setDescription(new SpannableString(desc).toString());
        new ShareAction(context).withMedia(img)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    private static void shareImg(Activity context,PlatformConfig.Platform platform, int picRes, CharSequence title, CharSequence desc, UMShareListener shareListener){
        UMImage img = new UMImage(context, picRes);
        img.setTitle(new SpannableString(title).toString());
        img.setDescription(new SpannableString(desc).toString());
        new ShareAction(context).withMedia(img)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    private static void shareUrl(Activity context, PlatformConfig.Platform platform, String url, CharSequence title, CharSequence desc, int picRes, UMShareListener shareListener){
        UMWeb web = new UMWeb(url);
        web.setTitle(new SpannableString(title).toString());
        web.setDescription(new SpannableString(desc).toString());
        web.setThumb(new UMImage(context, picRes));
        new ShareAction(context).withMedia(web)
                .setPlatform(platform.getName())
                .setCallback(shareListener)
                .share();
    }

    public void shareText(final Activity context, final View parent, final CharSequence text, final UMShareListener shareListener, final ShareBoardView.IOnBoardDismissListener boardListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareText(context, platform, text, shareListener);
            }
        }, boardListener);
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        board.show(parent, xy[0], xy[1]);
        showDialog.put(context, board);
    }

    public void shareImg(final Activity context, final View parent, final String url, final CharSequence title, final CharSequence desc, final UMShareListener shareListener, final ShareBoardView.IOnBoardDismissListener boardListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareImg(context, platform, url, title, desc, shareListener);
            }
        }, boardListener);
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        board.show(parent, xy[0], xy[1]);
        showDialog.put(context, board);
    }

    public void shareImg(final Activity context, final View parent, final int picRes, final CharSequence title, final CharSequence desc, final UMShareListener shareListener, final ShareBoardView.IOnBoardDismissListener boardListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareImg(context, platform, picRes, title, desc, shareListener);
            }
        }, boardListener);
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
        board.show(parent, xy[0], xy[1]);
        showDialog.put(context, board);
    }

    public void shareUrl(final Activity context, final View parent, final String url, final CharSequence title, final CharSequence desc, final int picRes, final UMShareListener shareListener, final ShareBoardView.IOnBoardDismissListener boardListener){
        ShareBoardView board = new ShareBoardView(context, new ShareBoardView.IOnShareItemClickListener() {
            @Override
            public void onItemClick(int position, PlatformConfig.Platform platform) {
                ShareUtils.shareUrl(context, platform, url, title, desc, picRes, shareListener);
            }
        }, boardListener);
        int[] xy = new int[2];
        parent.getLocationOnScreen(xy);
//        L.e("屏幕中的x:"+xy[0]+"\ty:"+xy[1]);
        board.show(parent, xy[0], xy[1]);
        showDialog.put(context, board);
    }

    public static void dismiss(Context context){
        if (context != null){
            ShareBoardView board = showDialog.get(context);
            if (board != null){
                board.dismiss();
            }
        }
    }
}
