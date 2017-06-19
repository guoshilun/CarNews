package com.jmgzs.carnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jmgzs.carnews.ui.MainFragment;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.fragment;

public class HomeAdapter extends FragmentPagerAdapter {

    private final static int PAGE_COUNT = 6;
    private final String[] titles = {"推荐", "新车", "行业", "导购", "用车", "头条"};
    private HashMap<String, Fragment> map;


    public HomeAdapter(FragmentManager fm) {
        super(fm);
        map = new HashMap<>();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = map.get(titles[position]);
        if (fragment == null) {
            Fragment mainFragment = new MainFragment();
            map.put(titles[position], mainFragment);
            fragment = mainFragment ;
        }
        return fragment;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment f = (Fragment) super.instantiateItem(container, position);
        return f;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);
        return titles[position];
    }


}
