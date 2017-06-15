package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmgzs.autoviewpager.AutoScrollViewPager;
import com.jmgzs.autoviewpager.indicator.CircleIndicator;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.AutoPagerAdapter;
import com.jmgzs.carnews.adapter.rcv.RCVAdapter;
import com.jmgzs.carnews.adapter.rcv.SpaceItemDecoration;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.base.BaseFragment;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.Photo;
import com.jmgzs.carnews.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/6/5.
 * Description:
 */

public class MainFragment extends BaseFragment implements OnRCVItemClickListener.OnItemClickListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RCVAdapter adapter = null;

    private View headerView;
    private AutoPagerAdapter headerAdapter;
    private AutoScrollViewPager headerPager;
    private CircleIndicator headerIndicator;

    private int lastVisibleItem = 0;
    private int page = 1;
    private boolean isPrepared = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        initView(v);
        isPrepared = true;
        lazyLoad();
        return v;
    }

    private void initView(View v) {
        recyclerView = getView(v, R.id.recycle);
        refreshLayout = getView(v, R.id.swiperefreshlayout);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.app_title_selected);
        refreshLayout.setColorSchemeResources(R.color.colorWhite);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (adapter != null) {
                    adapter.fillList(getNewsData(page));
                } else {
                    createAdapter();
                }
                refreshLayout.setRefreshing(false);
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        recyclerView.addItemDecoration(new SpaceItemDecoration(getActivity(), DividerItemDecoration.VERTICAL,2,R.color.app_red_color));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_IDLE == newState && lastVisibleItem + 1 == adapter.getItemCount()) {
                    //add more
                    adapter.appendList(getNewsData(page));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                L.e("onscroll--"+dx +","+dy+","+lastVisibleItem);
            }
        });

        headerView = View.inflate(getContext(), R.layout.layout_header_auto_view, null);
        headerPager = getView(headerView, R.id.auto_pager);
        headerIndicator = getView(headerView, R.id.stl_tab);
    }


    private void createHeaderAdapter() {
        headerAdapter = new AutoPagerAdapter(getContext(), null);
        headerPager.setInterval(3000);
        headerPager.setAdapter(headerAdapter);
        headerPager.setOffscreenPageLimit(1);
        headerPager.setAdapter(headerAdapter);
        headerIndicator.setViewPager(headerPager);
        headerAdapter.setBannerClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = headerPager.getCurrentItem();
                L.e("click header pos:" + pos);
                goNewsDetail(pos);
            }
        });
        if (headerPager.isCurrentAutoScroll())
            headerPager.startAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (headerPager != null && headerPager.getChildCount() > 1 && headerPager.isCurrentAutoScroll())
            headerPager.startAutoScroll();
    }

    @Override
    public void onPause() {
        headerPager.stopAutoScroll();
        super.onPause();
    }

    @Override
    protected void lazyLoad() {
        //load data
        if (getUserVisibleHint() && isPrepared) {
            createHeaderAdapter();
            createAdapter();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position < 0 || position > adapter.getItemCount()) return;
        L.e("click pos:" + position);
        goNewsDetail(position - adapter.getHeadersCount());
    }

    private void goNewsDetail(int pos) {
        startActivity(new Intent(getContext(), NewsDetailActivity.class));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void createAdapter() {
        adapter = new RCVAdapter(getContext(), getNewsData(page), this);
        adapter.addHeaderView(headerView);
        recyclerView.setAdapter(adapter);
    }


    private List<NewsDataBean> getNewsData(int page) {
        List<NewsDataBean> list = new ArrayList<>();
        for (int i = 0; i + (page - 1) * 20 < page * 20; i++) {
            if (i % 3 == 0) {
                ArrayList<Photo> photos = new ArrayList<>();
                photos.add(new Photo());
                photos.add(new Photo());
                photos.add(new Photo());
                list.add(new NewsDataBean("三图片", photos));
            }else {
                list.add(new NewsDataBean("一张图" + (i + (page - 1) * 20 + 1)));
            }
        }
        this.page++;
        return list;
    }
}
