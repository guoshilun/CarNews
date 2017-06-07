package com.jmgzs.carnews.ui.tab;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.jmgzs.carnews.R;
import com.ogaclejapan.smarttablelayout.SmartTabLayout;

import java.util.ArrayList;

public class HomeTabProvider implements SmartTabLayout.TabProvider {

    private ArrayList<TabItem> mInfos;
    private int padding;

    public HomeTabProvider(Context context) {
        mInfos = new ArrayList<>();
        padding = context.getResources().getDimensionPixelSize(R.dimen.top_tab_padding);
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        Context context = container.getContext();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        TabItem tab = new TabItem(context);
        tab.setLayoutParams(params);
        tab.setPadding(padding, padding, padding, padding);
        tab.setTextValue(adapter.getPageTitle(position).toString());
        tab.setTextColorNormal(R.color.app_title_normal);
        tab.setTextColorSelect(R.color.app_title_selected);
        if (position == 0) {
//            tab.setIconText(R.mipmap.icon_feather_gray, R.mipmap.icon_feather_red, null);
        } else {
//            tab.setIconText(R.mipmap.icon_msg_gray, R.mipmap.icon_msg_red, null);
//            tab.setDot(context.getResources().getColor(R.color.app_red_color), context.getResources().getDimension(R.dimen.home_top_tab_dot_radius));
        }

        mInfos.add(tab);

        return tab;
    }

    public TabItem getTabItem(int position) {
        if (position >= 0 && position < mInfos.size()) {
            return mInfos.get(position);
        } else {
            return null;
        }
    }

}
