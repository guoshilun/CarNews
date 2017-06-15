package com.jmgzs.carnews.ui.view;

import android.app.Activity;
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
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Arrays;

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

    public ShareBoardView(Activity context, IOnShareItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        initPop();
    }

    private void initPop(){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_board, (ViewGroup) context.findViewById(android.R.id.content), false);
        pop = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setTouchable(true);
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.style.pop_up_2_down_anim);

        initPopView(view);
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
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(View parent) {
        if (pop != null && !pop.isShowing()) {
            pop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
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
}
