package com.jmgzs.carnews.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.jmgzs.carnews.R;


public class DialogMenu extends Dialog implements
        View.OnClickListener {

    private Context ct;
    private TextView tvTop;
    private TextView tvMiddle1;
    private View dividerTop;
    private View divider1;
    private TextView tvMiddle2;
    private View divider2;
    private TextView tvBottom;
    private TextView tvCancle;

    private IMenuItemClickListerer listener;


    public static final int MENU_ITEM_TOP = -1;
    public static final int MENU_ITEM_MIDDLE_1 = 1;
    public static final int MENU_ITEM_MIDDLE_2 = 2;
    public static final int MENU_ITEM_BOTTOM = -2;

    public DialogMenu(Context context) {
        super(context, R.style.customDialog);
        this.ct = context;
        init();
        setAnimation();
    }

    private void init() {
        setContentView(R.layout.dialog_menu);
        initView();
        setLisenter();
    }

    private void initView() {
        tvTop = (TextView) findViewById(R.id.menu_item_top);
        tvBottom = (TextView) findViewById(R.id.menu_item_bottom);
        tvCancle = (TextView) findViewById(R.id.menu_cancle);

        tvMiddle1 = (TextView) findViewById(R.id.menu_item_middle1);
        tvMiddle2 = (TextView) findViewById(R.id.menu_item_middle2);

        divider1 = findViewById(R.id.menu_item_divider1);
        divider2 = findViewById(R.id.menu_item_divider2);
        dividerTop = findViewById(R.id.menu_item_divider_top);

    }

    private void setLisenter() {
        tvTop.setOnClickListener(this);
        tvMiddle1.setOnClickListener(this);
        tvMiddle2.setOnClickListener(this);
        tvBottom.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
    }

    private void setAnimation() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.color.colorTransparent);
        LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.dimAmount = 0.5f;
        lp.width = LayoutParams.MATCH_PARENT;
        window.addFlags(LayoutParams.FLAG_DIM_BEHIND);
        window.setWindowAnimations(R.style.roll_anim_style); // 设置窗口弹出动画
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.menu_item_top:
                if (listener != null) {
                    this.dismiss();
                    listener.onMenuItemClick(MENU_ITEM_TOP);
                }

                break;
            case R.id.menu_item_middle1:
                if (listener != null) {
                    this.dismiss();
                    listener.onMenuItemClick(MENU_ITEM_MIDDLE_1);
                }

                break;
            case R.id.menu_item_middle2:
                if (listener != null) {
                    this.dismiss();
                    listener.onMenuItemClick(MENU_ITEM_MIDDLE_2);
                }
                break;


            case R.id.menu_item_bottom:
                if (listener != null) {
                    this.dismiss();
                    listener.onMenuItemClick(MENU_ITEM_BOTTOM);
                }
                break;

            case R.id.menu_cancle:
                if (listener != null) {
                    this.dismiss();
                }
                break;

            default:
                break;
        }
    }

    public void setOnMenuItemClickListener(IMenuItemClickListerer listener) {
        this.listener = listener;
    }

    public void setSingleItemText(String s) {
        tvTop.setVisibility(View.VISIBLE);
        tvTop.setText(s);
        tvTop.setBackgroundResource(R.drawable.btn_dialog_single);
        tvMiddle1.setVisibility(View.GONE);
        tvMiddle2.setVisibility(View.GONE);
        tvBottom.setVisibility(View.GONE);
        divider1.setVisibility(View.GONE);
        divider2.setVisibility(View.GONE);
        dividerTop.setVisibility(View.GONE);

    }

    public void setMenuItemTopText(int visible, String s) {
        tvTop.setVisibility(visible);
        tvTop.setText(s);
    }

    public void setMenuItemBottomText(int visible, String s) {
        tvBottom.setVisibility(visible);
        tvBottom.setText(s);
    }

    public void setMenuItemMiddle1Text(int visible, String s) {
        tvMiddle1.setVisibility(visible);
        divider1.setVisibility(visible);
        tvMiddle1.setText(s);
    }

    public void setMenuItemMiddle2Text(int visible, String s) {
        tvMiddle2.setVisibility(visible);
        divider2.setVisibility(visible);
        tvMiddle2.setText(s);
    }


}
