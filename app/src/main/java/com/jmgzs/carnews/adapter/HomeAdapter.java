package com.jmgzs.carnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jmgzs.carnews.ui.MainFragment;

import java.util.ArrayList;

public class HomeAdapter extends FragmentPagerAdapter {

    private final static int PAGE_COUNT = 6;
    private final String[] titles = {"推荐","新车","行业","导购","用车","头条"};
    private MainFragment mainFragment;


    public HomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            default:
//                if (null == mainFragment)
                    mainFragment = new MainFragment();
                return mainFragment;
//        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment =  (Fragment) super.instantiateItem(container, position);
        return fragment;
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
