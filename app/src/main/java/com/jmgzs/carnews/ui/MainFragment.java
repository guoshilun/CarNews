package com.jmgzs.carnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jmgzs.autoviewpager.AutoScrollViewPager;
import com.jmgzs.autoviewpager.indicator.CircleIndicator;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.AutoPagerAdapter;
import com.jmgzs.carnews.adapter.rcv.RCVAdapter;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.base.BaseFragment;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.NewsListBean;

import com.jmgzs.carnews.network.Urls;
import com.jmgzs.lib_network.network.ConfigCache;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.L;

import com.jmgzs.carnews.util.T;

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
    private boolean isPrepared = false;
    //    private ArrayList<NewsDataBean> list;
//    private ArrayList<NewsDataBean> headerList = new ArrayList<>();


    private static final int pageCount = 5;
    private boolean loadAll = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        initView(v);
        isPrepared = true;
//        if (savedInstanceState != null) {
//            currentPos = savedInstanceState.getInt("pos");
//            startKey = savedInstanceState.getInt("sk");
//        }
        L.e("oncreate view----" + currentPos + ",sk:" + startKey);
        lazyLoad1();
        return v;
    }


    private void initView(View v) {
        recyclerView = getView(v, R.id.recycle);
        refreshLayout = getView(v, R.id.swiperefreshlayout);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorWhite);
        refreshLayout.setColorSchemeResources(R.color.app_title_selected);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startKey = 0;
                getData(startKey);
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
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    if (adapter != null && lastVisibleItem + 1 == adapter.getItemCount() && !loadAll) {
                        getData(startKey);
                    }
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

        headerPager.setInterval(3000);
        headerPager.setOffscreenPageLimit(3);

    }


    private void createHeaderAdapter(ArrayList<NewsDataBean> header) {
        headerAdapter = new AutoPagerAdapter(getActivity(), header);
        setHeaderAdapter(headerAdapter);
    }

    private void setHeaderAdapter(final AutoPagerAdapter adapter) {
        headerPager.setAdapter(adapter);
        headerIndicator.setViewPager(headerPager);
        adapter.setBannerClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = headerPager.getCurrentItem();
                L.e("click header pos:" + pos);
                goNewsDetail(adapter.getItem(pos).getAid());
            }
        });
        if (headerPager.isCurrentAutoScroll())
            headerPager.startAutoScroll();

    }


    private void createAdapter(List<NewsDataBean> data) {
        adapter = new RCVAdapter(getActivity(), data, this);
        setPagerAdapter(adapter);
    }

    private void setPagerAdapter(RCVAdapter adapter) {
        adapter.addHeaderView(headerView);
        recyclerView.setAdapter(adapter);

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


    protected void lazyLoad1() {
        if (adapter == null || adapter.getDataCount() == 0) {
            initAdapter(Urls.getUrlNews(String.valueOf(0), getChannel()), getNewsDataCache(0), false);
            startKey = 0;
            getData(startKey);
        }else {
            createHeaderAdapter(headerAdapter.getData());
            createAdapter(adapter.getData());
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position < 0 || position > adapter.getItemCount()) return;
        L.e("click pos:" + position);
        goNewsDetail(adapter.getItem(position - adapter.getHeadersCount()).getAid());
    }

    private void goNewsDetail(int aid) {
        Intent in = new Intent(getContext(), NewsInfoActivity.class);
        in.putExtra("aid", aid);
        startActivity(in);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
    }


    private void getData(final int sk) {
        L.e("startKey ==" + sk);
        final String url = Urls.getUrlNews(String.valueOf(sk), getChannel());
        RequestUtil.requestByGetAsy(getContext(), url, NewsListBean.class, new IRequestCallBack<NewsListBean>() {

            @Override
            public void onSuccess(String url, NewsListBean data) {
                L.e("date news size:" + data.getData().size());
                initAdapter(url, data, true);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                T.toastS("数据请求失败,请稍后重试。");
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancel(String url) {
                refreshLayout.setRefreshing(false);

            }
        });
    }

    private void initAdapter(String url, NewsListBean data, boolean saveCache) {
        if (data == null) return;
        if (data.getData() == null || data.getData().size() == 0) {
            if (getUserVisibleHint()) {
                loadAll = true;
                T.toastS(getContext(), "暂无更多新闻");
            }
            return;
        }
        loadAll = false;
        ArrayList<NewsDataBean> list = data.getData();
        if (adapter == null || adapter.getDataCount() == 0) {//第一次
            if (saveCache) ConfigCache.setUrlCache(getContext(), url, data.toString());
            ArrayList<NewsDataBean> headerList = new ArrayList<>();
            if (list.size() <= pageCount) {
                headerList.add(list.remove(0));
            } else
                for (int i = 0; i < pageCount; i++) {
                    headerList.add(list.remove(0));
                }
            createHeaderAdapter(headerList);
            createAdapter(list);
        } else {
            if (startKey == 0) {//下拉刷新
                if (saveCache) ConfigCache.setUrlCache(getContext(), url, data.toString());
                ArrayList<NewsDataBean> headerList = new ArrayList<>(5);
                if (list.size() <= pageCount) {
                    headerList.add(list.remove(0));
                } else
                    for (int i = 0; i < pageCount; i++) {
                        headerList.add(list.remove(0));
                    }
                headerAdapter.updateData(headerList);
                adapter.fillList(list);
            } else {
                adapter.appendList(list);
            }
        }
        if (saveCache)
            startKey = data.getNext_key();
        L.w("response最新key:" + startKey);
    }


    private NewsListBean getNewsDataCache(int sk) {
        L.e("使用缓存");
        String json = ConfigCache.getUrlCache(getContext(), Urls.getUrlNews(String.valueOf(sk), getChannel()));
        Gson g = new Gson();
        try {
            NewsListBean dataBean = g.fromJson(json, NewsListBean.class);
            return dataBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
