package com.jmgzs.carnews.ui;

import android.app.Activity;
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

import com.google.gson.Gson;
import com.jmgsz.lib.adv.AdvTempList;
import com.jmgsz.lib.adv.enums.AdSlotType;
import com.jmgsz.lib.adv.utils.DensityUtils;
import com.jmgzs.autoviewpager.AutoScrollViewPager;
import com.jmgzs.autoviewpager.indicator.CircleIndicator;
import com.jmgzs.carnews.R;
import com.jmgzs.carnews.adapter.AutoPagerAdapter;
import com.jmgzs.carnews.adapter.rcv.RCVAdapter;
import com.jmgzs.carnews.adapter.rcvbase.OnRCVItemClickListener;
import com.jmgzs.carnews.base.BaseFragment;
import com.jmgzs.carnews.bean.AdvDataBean;
import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.NewsListBean;

import com.jmgzs.carnews.network.Urls;
import com.jmgzs.carnews.util.ResUtils;
import com.jmgzs.lib_network.network.ConfigCache;
import com.jmgzs.lib_network.network.IRequestCallBack;
import com.jmgzs.lib_network.network.RequestUtil;
import com.jmgzs.lib_network.utils.L;

import com.jmgzs.carnews.util.T;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.data;
import static android.R.id.list;
import static android.media.CamcorderProfile.get;
import static com.jmgsz.lib.adv.AdvTempList.getList_600_300;
import static com.umeng.message.proguard.k.A;
import static com.umeng.message.proguard.k.k;


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
    private boolean isLoading = false;

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
                    if (adapter != null && lastVisibleItem + 1 == adapter.getItemCount() && !isLoading) {
                        if (loadAll){
                            T.toastS(getContext(), "暂无更多新闻");
                        }else {
                            isLoading = !isLoading;
                            getData(startKey);
                        }
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
                goNewsDetail(adapter.getItem(pos));
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
        if (headerPager != null && headerPager.getChildCount() > 1 && headerPager.isCurrentAutoScroll() && getUserVisibleHint())
            headerPager.startAutoScroll();
    }

    @Override
    public void onPause() {
        headerPager.stopAutoScroll();
        super.onPause();
    }


    protected void lazyLoad1() {
        //请求广告
        Activity activity = getActivity();
        int width = DensityUtils.getScreenWidthPixels(activity) - DensityUtils.dip2px(activity, 8 * 2);
        if (getCurrentPos() == 0) {
            AdvTempList.requestAdvTempList(activity, width, 5, AdSlotType.INFO_600_300_W);
        } else if (getCurrentPos() == 2) {
            AdvTempList.requestAdvTempList(activity, width, 5, AdSlotType.INFO_720_405_W);
        } else if (getCurrentPos() == 4) {
            AdvTempList.requestAdvTempList(activity, width, 5, AdSlotType.INFO_800_120_W);
        }
        //请求新闻列表
        if (adapter == null || adapter.getDataCount() == 0) {
            initAdapter(Urls.getUrlNews(String.valueOf(0), getChannel()), getNewsDataCache(0), false);
            startKey = 0;
            getData(startKey);
        } else {
            createHeaderAdapter(headerAdapter.getData());
            createAdapter(adapter.getData());
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position < 0 || position > adapter.getItemCount()) return;
        L.e("click pos:" + position);
        goNewsDetail(adapter.getItem(position - adapter.getHeadersCount()));
    }

    private void goNewsDetail(NewsDataBean dataBean) {
        Intent in = new Intent(getContext(), NewsInfoActivity.class);
        in.putExtra("aid", dataBean.getAid());
        if (dataBean.getImg_list() != null)
            in.putStringArrayListExtra("images", new ArrayList<>(dataBean.getImg_list()));
        else
            in.putStringArrayListExtra("images", new ArrayList<String>());
        in.putExtra("channel", getChannel());
        startActivity(in);

    }


    private void getData(final int sk) {
        L.e("startKey ==" + sk);
        final String url = Urls.getUrlNews(String.valueOf(sk), getChannel());
        RequestUtil.requestByGetAsy(getContext(), url, NewsListBean.class, new IRequestCallBack<NewsListBean>() {

            @Override
            public void onSuccess(String url, NewsListBean data) {
                if (!ResUtils.processResponse(url, data, this)) {
                    return;
                }
                L.e("date news size:" + data.getData().size());
                initAdapter(url, data, true);
                refreshLayout.setRefreshing(false);
                isLoading = false;
            }

            @Override
            public void onFailure(String url, int errorCode, String msg) {
                T.toastS("数据请求失败,请稍后重试。");
                refreshLayout.setRefreshing(false);
                isLoading = false;
            }

            @Override
            public void onCancel(String url) {
                refreshLayout.setRefreshing(false);
                isLoading = false;

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
            createHeaderAdapter(headerList);//header
            addAdvsIntoNews(list);//adv
            createAdapter(list);//列表
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
                addAdvsIntoNews(list);
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
        String json = ConfigCache.getUrlCacheDefault(getContext(), Urls.getUrlNews(String.valueOf(sk), getChannel()));
        Gson g = new Gson();
        try {
            NewsListBean dataBean = g.fromJson(json, NewsListBean.class);
            return dataBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addAdvsIntoNews(List<NewsDataBean> data) {
        for (int i = 3; i < data.size();) {
            AdvDataBean bean = createAdvItemData();
            if (bean != null) {
                data.add(i, bean);
                i += 4;
            } else break;
        }
    }

    private AdvDataBean createAdvItemData() {
        AdvDataBean ad = new AdvDataBean();
        List<AdvTempList.AdvTempBean> cache = null;
        if (getCurrentPos() == 0) {
            cache = AdvTempList.getList_600_300();
        } else if (getCurrentPos() == 2) {
            cache = AdvTempList.getList_720_405();
        } else if (getCurrentPos() == 4) {
            cache = AdvTempList.getList_800_120();
        }
        if (cache != null && cache.size() > 0) {
            ad.setHtml(cache.get(0).getHtml());
            ad.setAdvW(cache.get(0).getWidth());
            ad.setAdvH(cache.get(0).getHeight());
            ad.setFile(cache.get(0).getFile());
            return ad;
        } else {
            return null;
        }
    }
}
