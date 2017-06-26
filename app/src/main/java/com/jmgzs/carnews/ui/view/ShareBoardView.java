package com.jmgzs.carnews.ui.view;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.ShareGridAdapter;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Arrays;

import static android.os.Build.VERSION_CODES.N;

/**
 * 分享面板
 * Created by Wxl on 2017/6/15.
 */

public class ShareBoardView {

    private Activity context;
    private PopupWindow pop;
    private GridView gridView;
    private View btnDismiss;
    private IOnShareItemClickListener listener;
    private IOnBoardDismissListener boardListener;
    private int height;

    public ShareBoardView(Activity context, IOnShareItemClickListener listener, IOnBoardDismissListener boardListener) {
        this.context = context;
        this.listener = listener;
        this.boardListener =boardListener;
        initPop();
    }

    private void initPop(){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_board, (ViewGroup) context.findViewById(android.R.id.content), false);
        pop = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setTouchable(true);
        pop.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.style.pop_up_2_down_anim);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (boardListener != null){
                    boardListener.onDismiss(true);
                }
            }
        });

        initPopView(view);
        view.measure(View.MeasureSpec.makeMeasureSpec(DensityUtils.getScreenWidthPixels(context),View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(DensityUtils.getScreenHeightPixels(context), View.MeasureSpec.AT_MOST));
        height = view.getMeasuredHeight();
    }

    private void initPopView(View view){
        gridView = (GridView) view.findViewById(R.id.layoutShareBoard_gv_table);
        btnDismiss = view.findViewById(R.id.layoutShareBoard_img_arrow);

        gridView.setAdapter(new ShareGridAdapter(context, Arrays.asList(PlatformConfig.getPlatform(SHARE_MEDIA.WEIXIN), PlatformConfig.getPlatform(SHARE_MEDIA.WEIXIN_CIRCLE), PlatformConfig.getPlatform(SHARE_MEDIA.QQ), PlatformConfig.getPlatform(SHARE_MEDIA.QZONE))));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null){
                    listener.onItemClick(position, (PlatformConfig.Platform) parent.getItemAtPosition(position));
                }
                dismiss();
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     *
     * @param parent
     * @param x
     * @param y pop将会显示在该参数的坐标上方
     */
    public void show(View parent, int x, int y) {
        if (pop != null && !pop.isShowing()) {
            pop.showAtLocation(parent, Gravity.NO_GRAVITY, x, y - height);
            if (boardListener != null){
                boardListener.onDismiss(false);
            }
        }
    }

    public void dismiss() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }

    public interface IOnShareItemClickListener{
        void onItemClick(int position, PlatformConfig.Platform platform);
    }

    public interface IOnBoardDismissListener{
        void onDismiss(boolean isDismiss);
    }
}
