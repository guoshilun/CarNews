package com.jmgzs.carnews.base;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jmgzs.carnews.R;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.carnews.push.PushUtil;
import com.jmgzs.carnews.util.Const;
import com.jmgzs.carnews.util.SPBase;
import com.jmgzs.lib_network.utils.L;

import static com.umeng.message.proguard.k.A;

/**
 * Created by mac on 17/6/5.
 * Description:
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected LinearLayout root;
    protected View paddingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        translucentStatusBar();
        L.setTag(getClass().getSimpleName());
        PushUtil.getPush().activityInit(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        parent.removeAllViews();
        parent.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(getContent(savedInstanceState));
        initView();
    }

    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected int getStatusBarColor() {
        return this.getResources().getColor(R.color.colorPrimary);
    }

    public void setStatusBarColor(int color) {
        if (paddingView != null) {
            paddingView = new View(this);
            paddingView.setBackgroundColor(color);
        }
    }

    protected void addPaddingAboveContentView() {
        int statusBarHeight = DensityUtils.getStatusBarHeight(this);
        if (paddingView == null) {
            paddingView = new View(this);
        } else {
            ((ViewGroup) paddingView.getParent()).removeView(paddingView);
        }
        paddingView.setBackgroundColor(getStatusBarColor());
//        L.e("状态栏高度："+statusBarHeight);
        root.addView(paddingView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
    }

    protected void setTheme() {
        int type = SPBase.getInt(Const.SPKey.TEXT_SIZE, 1);
        if (type == 0)
            setTheme(R.style.Text_Size_Small);
        else if (type == 1)
            setTheme(R.style.Default_Text_Size_Middle);
        else
            setTheme(R.style.Text_Size_Big);

    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration() ;//res.getConfiguration();
//        config.fontScale = 2f;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        ViewGroup parent = root;
        root.removeAllViews();
        LayoutInflater.from(this).inflate(layoutResID, parent, true);
        addPaddingAboveContentView();
    }

    @Override
    public void setContentView(View view) {
        ViewGroup parent = root;
        root.removeAllViews();
        parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addPaddingAboveContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        ViewGroup parent = root;
        root.removeAllViews();
        parent.addView(view, params);
        addPaddingAboveContentView();
    }

    @Override
    public void onClick(View view) {

    }

    protected abstract int getContent(Bundle save);

    protected abstract void initView();


    protected <E extends View> E getView(int resID) {
        try {
            return (E) findViewById(resID);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    protected <E extends View> E getView(View rootView, int resID) {
        try {
            if (null != rootView) {
                return (E) findViewById(resID);
            } else throw new NullPointerException();
        } catch (Exception e) {
            throw e;
        }
    }
}
