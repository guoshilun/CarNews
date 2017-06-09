package com.jmgzs.carnews.adapter.rcvbase;

import android.view.View;

/**
 * Created by XJ on 2016/9/23.
 */
public class OnRCVItemClickListener {


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);

    }

}
