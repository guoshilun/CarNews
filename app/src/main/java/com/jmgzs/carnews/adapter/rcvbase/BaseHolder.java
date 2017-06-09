package com.jmgzs.carnews.adapter.rcvbase;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by XJ on 2016/9/23.
 */
public abstract class BaseHolder<D> extends RecyclerView.ViewHolder {


    public BaseHolder(ViewGroup parent, @LayoutRes int resId) {
        this(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));
    }

    public BaseHolder(View item){
        super(item);
    }

    /**
     * 获取布局中的View
     *
     * @param viewId view的Id
     * @param <T>    View的类型
     * @return view
     */
    protected <T extends View> T getView(@IdRes int viewId) {
        return (T) (itemView.findViewById(viewId));
    }

    /**
     * 获取Context实例
     *
     * @return context
     */
    protected Context getContext() {
        return itemView.getContext();
    }

    /**
     * 设置数据
     * @param data 要显示的数据对象
     */
    public abstract void setData(D data);
}
