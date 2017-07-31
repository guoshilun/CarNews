package com.jmgsz.lib.adv.ui;

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

import com.jmgsz.lib.adv.R;
import com.jmgsz.lib.adv.utils.DensityUtils;

/**
 * Created by Wxl on 2017/7/19.
 */

public abstract class AdvBaseActivity extends AppCompatActivity {
    protected LinearLayout root;
    protected View paddingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        translucentStatusBar();

        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        parent.removeAllViews();
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
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

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        ViewGroup parent = root;
        root.removeAllViews();
        LayoutInflater.from(this).inflate(layoutResID, parent, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//有透明状态栏的版本才添加顶部paddingView
            addPaddingAboveContentView();
        }
    }

    @Override
    public void setContentView(View view) {
        ViewGroup parent = root;
        root.removeAllViews();
        parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//有透明状态栏的版本才添加顶部paddingView
            addPaddingAboveContentView();
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        ViewGroup parent = root;
        root.removeAllViews();
        parent.addView(view, params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//有透明状态栏的版本才添加顶部paddingView
            addPaddingAboveContentView();
        }
    }

    protected abstract int getContent(Bundle save);

    protected abstract void initView();
}
