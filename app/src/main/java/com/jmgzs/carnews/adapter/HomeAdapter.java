package com.jmgzs.carnews.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseFragment;
import com.jmgzs.carnews.ui.MainFragment;
import com.jmgzs.lib_network.utils.L;

import java.util.HashMap;

public class HomeAdapter extends FragmentStatePagerAdapter {

    private String[] titles = null;
    private HashMap<String, Fragment> map;

    public HomeAdapter(FragmentManager fm, Context ct) {
        super(fm);
        map = new HashMap<>();
        titles = ct.getResources().getStringArray(R.array.tab_titles);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = map.get(titles[position]);
        if (fragment == null) {
            MainFragment mainFragment = new MainFragment();
            map.put(titles[position], mainFragment);
            fragment = mainFragment;
        } else {
            L.e("重用fragment pos:" + position);
        }
        ((BaseFragment) fragment).updatePosition(position);
        return fragment;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment f = (Fragment) super.instantiateItem(container, position);
        return f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        map.put(titles[position], (Fragment) object);
        L.e("缓存一个fragment pos:" + position);
    }

//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }

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
