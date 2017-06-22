package com.jmgzs.carnews.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jmgzs.carnews.R;

/**
 * Created by mac on 17/6/13.
 * Description:
 */

public class SettingItemView extends RelativeLayout implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cbox;
    private TextView tvTitle;
    private TextView tvTips;
    private TextView tvState;
    private ImageView arrow;

    private OnCheckChangedListener listener;


    public SettingItemView(Context context) {
        super(context);
        init(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public interface OnCheckChangedListener {
        void onCheckChangedListener(View buttonView, boolean isChecked);
    }


    private void init(Context ct) {
        View v = View.inflate(ct, R.layout.item_view_setting, null);
        cbox = (CheckBox) v.findViewById(R.id.setting_item_cbox);
        tvTitle = (TextView) v.findViewById(R.id.setting_item_title_text);
        tvTips = (TextView) v.findViewById(R.id.setting_item_tips_tv);
        tvState = (TextView) v.findViewById(R.id.setting_item_tips2_tv);
        arrow = (ImageView) v.findViewById(R.id.setting_item_right_image);

        addView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        cbox.setOnCheckedChangeListener(this);
    }

    public void setChecked(boolean checked) {
        cbox.setChecked(checked);
    }

    public boolean isChecked() {
        return cbox.isChecked();
    }

    public void setTextTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTextTitle(int resId) {
        tvTitle.setText(resId);
    }

    public void setTvTips(String tips){
        tvTips.setText(tips);
    }
    public void setTvTips(Integer tipsId){
        tvTips.setText(tipsId);
    }

    public void setTextState(String state) {
        tvState.setText(state);
    }

    public CharSequence getTextState(){
        return tvState.getText();
    }

    public void setTextState(int resId) {
        tvState.setText(resId);
    }

    public void setTextTitleSize(int resId) {
        tvTitle.setTextSize(resId);
    }

    public void showTips(boolean show) {
        tvTips.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void showState(boolean show) {
        tvState.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void showCheckBox(boolean show) {
        cbox.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void showArrow(boolean show) {
        arrow.setVisibility(show ? VISIBLE : INVISIBLE);
    }


    public void show(boolean showTips, boolean showState, boolean showCheckBox, boolean showArrow) {
        showTips(showTips);
        showState(showState);
        showCheckBox(showCheckBox);
        showArrow(showArrow);
    }

    public void setonCheckChangedListener(OnCheckChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (listener != null) {
            listener.onCheckChangedListener(this, isChecked);
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measureWidth = measureWidth(0, widthMeasureSpec);
//        int measureHeight = measureHeight(0, heightMeasureSpec);
//        for (int i = 0; i < getChildCount(); i++) {
//            View v = getChildAt(i);
//
//            int widthSpec = 0;
//            int heightSpec = 0;
//            ViewGroup.LayoutParams params = v.getLayoutParams();
//            if (params.width > 0) {
//                widthSpec = MeasureSpec.makeMeasureSpec(params.width,
//                        MeasureSpec.EXACTLY);
//            } else if (params.width == -1) {
//                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
//                        MeasureSpec.EXACTLY);
//            } else if (params.width == -2) {
//                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
//                        MeasureSpec.AT_MOST);
//            }
//
//            if (params.height > 0) {
//                heightSpec = MeasureSpec.makeMeasureSpec(params.height,
//                        MeasureSpec.EXACTLY);
//            } else if (params.height == -1) {
//                heightSpec = MeasureSpec.makeMeasureSpec(measureHeight,
//                        MeasureSpec.EXACTLY);
//            } else if (params.height == -2) {
//                heightSpec = MeasureSpec.makeMeasureSpec(measureWidth,
//                        MeasureSpec.AT_MOST);
//            }
//            v.measure(widthSpec, heightSpec);
//
//        }
//        // 设置自定义的控件MyViewGroup的大小
//        setMeasuredDimension(measureWidth, measureHeight);
//    }
//
//    private int measureWidth(int size, int pWidthMeasureSpec) {
//        int result = size;
//        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
//        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
//
//        switch (widthMode) {
//            /**
//             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
//             * MeasureSpec.AT_MOST。
//             *
//             *
//             * MeasureSpec.EXACTLY是精确尺寸，
//             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
//             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
//             *
//             *
//             * MeasureSpec.AT_MOST是最大尺寸，
//             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
//             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
//             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
//             *
//             *
//             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
//             * 通过measure方法传入的模式。
//             */
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.EXACTLY:
//                result = widthSize;
//                break;
//        }
//        return result;
//    }
//
//    private int measureHeight(int size, int pHeightMeasureSpec) {
//        int result = size;
//
//        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
//
//        switch (heightMode) {
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.EXACTLY:
//                result = heightSize;
//                break;
//        }
//        return result;
//    }

}
