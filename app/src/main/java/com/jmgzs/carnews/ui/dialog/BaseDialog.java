package com.jmgzs.carnews.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.jmgzs.carnews.R;
import com.jmgzs.lib_network.utils.L;


/**
 * Created by zhaotun on 15/11/9.
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener {

    protected Context context;

    protected float mDimAmount = 0.6f;

    protected float mWidthScale = 0.9f;

    protected int gravity = Gravity.CENTER_VERTICAL;

    protected int animId = -1;

    protected int x = 0;

    protected int y = 0;

    protected int h = LayoutParams.WRAP_CONTENT;

    protected OnDialogClickListener mOnDialogClickListener;

    /**
     * Create a Dialog window that uses a custom dialog style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */
    public BaseDialog(Context context) {
        super(context, R.style.customDialog_roll_up_down);
        this.context = context;
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        create(savedInstanceState);
        initView();
        setWindow(x, y);
    }

    protected void setWindow(int x, int y) {
        Window dialogWindow = getWindow();
        LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * mWidthScale);
        lp.x = x;
        lp.y = y;
        lp.height = h;
        lp.gravity = gravity;
        lp.dimAmount = mDimAmount;
        dialogWindow.addFlags(LayoutParams.FLAG_DIM_BEHIND);

        if (animId != -1) {
            dialogWindow.setWindowAnimations(animId);
        }
    }

    @Override
    public void onClick(View v) {

    }


    protected <E extends View> E getView(View v, int id) {
        try {
            if (v != null) {
                return (E) v.findViewById(id);
            } else {
                throw new NullPointerException("root view is null, can not find view");
            }
        } catch (Throwable e) {
            L.e("Could not cast View to concrete class");
            return null;
        }
    }

    protected <E extends View> E getView(int id) {
        return getView(getWindow().getDecorView(), id);
    }


    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.mOnDialogClickListener = listener;
    }

    protected abstract void create(Bundle b);

    protected abstract void initView();

    public interface OnDialogClickListener {

        void onClick(DialogInterface dialog, int i);
    }
}
