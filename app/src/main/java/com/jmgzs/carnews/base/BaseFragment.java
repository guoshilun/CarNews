package com.jmgzs.carnews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jmgzs.carnews.R;
import com.jmgzs.lib_network.utils.L;


public abstract class BaseFragment extends Fragment {

    protected int currentPos = 0;//当前viewpager选择的位置
    protected int startKey = 0;//请求起始位置key

    protected String[] titles;
    protected String[] channels;

//    protected boolean isFirstVisible = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        titles = getResources().getStringArray(R.array.tab_titles);
        channels = getResources().getStringArray(R.array.tab_channels);
        super.onCreate(savedInstanceState);
        L.setTag(this.getClass().getSimpleName());

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
            L.e(" visible page-fragment pos:" + currentPos);
        } else {
            onInVisible();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", currentPos);
        outState.putInt("sk", startKey);
        L.e("save  position:" + currentPos + ",sk:" + startKey);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            currentPos = savedInstanceState.getInt("pos");
//            startKey = savedInstanceState.getInt("sk");
//        }
        L.e("restored  position:" + currentPos + ", sk:" + startKey);
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInVisible() {

    }

    protected void lazyLoad() {
    }

    public void updatePosition(int position) {
        this.currentPos = position;
        L.e("update new pos:" + currentPos);
    }

    protected String getChannel() {
        if (currentPos < 0 || currentPos > channels.length)
            currentPos = 0;
        return channels[currentPos];
    }


    protected <E extends View> E getView(View rootView, int resViewId) {
        try {
            if (rootView != null) {
                return (E) rootView.findViewById(resViewId);
            } else {
                throw new NullPointerException("root view is null, can not find view");
            }
        } catch (Throwable e) {
            throw e;
        }
    }

    protected <E extends View> E getView(int resViewId) {
        try {
            if (getView() != null)
                return (E) getView().findViewById(resViewId);
            else {
                throw new NullPointerException("root view is null, can not find view");
            }
        } catch (Throwable e) {
            throw e;
        }
    }
}
