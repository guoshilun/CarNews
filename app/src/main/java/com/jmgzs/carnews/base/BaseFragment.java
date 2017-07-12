package com.jmgzs.carnews.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.util.UmengUtil;
import com.jmgzs.lib_network.utils.L;

import java.util.HashMap;
import java.util.Map;


public abstract class BaseFragment extends Fragment {

    protected int currentPos = 0;//当前viewpager选择的位置
    protected int startKey = 0;//请求起始位置key

    protected String[] titles;
    protected String[] channels;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initRes();
    }

    private void initRes() {
        titles = getContext().getResources().getStringArray(R.array.tab_titles);
        channels = getContext().getResources().getStringArray(R.array.tab_channels);
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
//        L.e("restored  position:" + currentPos + ", sk:" + startKey);
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInVisible() {

    }

    protected void lazyLoad() {
        Map<String, String> map = new HashMap<>();
        if (getContext() != null)
            map.put(UmengUtil.U_CHANNEL_NAME, getChannel());
        else {
            String channel = null;
            switch (currentPos) {
                case 0:
                    channel = "热点";
                    break;
                case 1:
                    channel = "新车";
                    break;
                case 2:
                    channel = "解析";
                    break;
                case 3:
                    channel = "行业";
                    break;
                case 4:
                    channel = "销量";
                    break;
                case 5:
                    channel = "动态";
                    break;
                case 6:
                    channel = "导购";
                    break;
                case 7:
                    channel = "试驾";
                    break;
                case 8:
                    channel = "使用";
                    break;
                default:
                    channel = "unknown";
                    break;
            }
            map.put(UmengUtil.U_CHANNEL_NAME, channel);

        }
        map.put(UmengUtil.U_CHANNEL_COUNT, "1");
        UmengUtil.event(getContext(), UmengUtil.U_CHANNEL, map);

    }

    public void updatePosition(int position) {
        this.currentPos = position;
        L.e("update new pos:" + currentPos);
    }

    public int getCurrentPos() {
        return currentPos;
    }

    protected String getChannel() {
        if (channels == null) initRes();
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
