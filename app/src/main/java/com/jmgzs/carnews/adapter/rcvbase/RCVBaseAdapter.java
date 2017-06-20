package com.jmgzs.carnews.adapter.rcvbase;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.jmgzs.carnews.bean.NewsDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XJ on 2016/9/23.
 */
public abstract class RCVBaseAdapter<D, T extends BaseHolder> extends RecyclerView.Adapter<T> {

    protected OnRCVItemClickListener.OnItemClickListener itemClickListener;
    protected OnRCVItemClickListener.OnItemLongClickListener itemLongClickListener;

    protected List<D> data;

    protected boolean mIsStaggeredGrid = false;

    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    private static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;
    private final List<FixedViewInfo> mHeaderViewInfoList = new ArrayList<>();
    private final List<FixedViewInfo> mFooterViewInfoList = new ArrayList<>();

    public static class FixedViewInfo {
        public int viewType;
        public View view;
    }

    public RCVBaseAdapter(@Nullable List<D> data) {
        this(data, null);
    }

    public RCVBaseAdapter(@Nullable List<D> data, OnRCVItemClickListener.OnItemClickListener itemClickListener) {
        this.data = data;
        this.itemClickListener = itemClickListener;
        if (this.data == null)
            this.data = new ArrayList<>();

    }

    public void setItemClickListener(OnRCVItemClickListener.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnRCVItemClickListener.OnItemLongClickListener itemClickListener) {
        this.itemLongClickListener = itemClickListener;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderViewType(viewType)) {
            int headerIndex = Math.abs(viewType - BASE_HEADER_VIEW_TYPE);
            View headerView = mHeaderViewInfoList.get(headerIndex).view;
            return (T) createHeaderFooterViewHolder(headerView);
        } else if (isFooterViewType(viewType)) {
            int footerIndex = Math.abs(viewType - BASE_FOOTER_VIEW_TYPE);
            View footerView = mFooterViewInfoList.get(footerIndex).view;
            return (T) createHeaderFooterViewHolder(footerView);
        } else {
            return this.onCreateVH(parent, viewType);
        }
    }

    public abstract T onCreateVH(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final T holder, int position) {
        if (position < 0 || position >= getHeadersCount() + getDataCount()) {
            return;
        }

        if (data != null && data.size() > 0) {
            if (getHeadersCount() > 0 && position >= getHeadersCount())
                holder.setData(data.get(position - getHeadersCount()));
            else
                holder.setData(data.get(position));
        }
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }
        if (itemLongClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemLongClickListener.onItemLongClick(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return getFootersCount() + getHeadersCount() + getDataCount();
    }


    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViewInfoList.get(position).viewType;
        } else if (isFooterPosition(position)) {
            return mFooterViewInfoList.get(position - getDataCount() - getHeadersCount()).viewType;
        } else {
            return getViewType(position);
        }
    }

    public abstract int getViewType(int pos);

    public boolean removeFooterView(View v) {
        for (int i = 0; i < mFooterViewInfoList.size(); i++) {
            FixedViewInfo info = mFooterViewInfoList.get(i);
            if (info.view == v) {
                mFooterViewInfoList.remove(i);
                notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    public void removeAllHeaderView() {
        if (!mHeaderViewInfoList.isEmpty()) {
            mHeaderViewInfoList.clear();
            notifyDataSetChanged();
        }
    }

    public void removeAllFooterView() {
        if (!mFooterViewInfoList.isEmpty()) {
            mFooterViewInfoList.clear();
            notifyDataSetChanged();
        }
    }

    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + mHeaderViewInfoList.size();
        mHeaderViewInfoList.add(info);
//        notifyDataSetChanged();
    }

    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_FOOTER_VIEW_TYPE + mFooterViewInfoList.size();
        mFooterViewInfoList.add(info);
//        notifyDataSetChanged();
    }

    public boolean containsFooterView(View v) {
        for (int i = 0; i < mFooterViewInfoList.size(); i++) {
            FixedViewInfo info = mFooterViewInfoList.get(i);
            if (info.view == v) {
                return true;
            }
        }
        return false;
    }

    public boolean containsHeaderView(View v) {
        for (int i = 0; i < mHeaderViewInfoList.size(); i++) {
            FixedViewInfo info = mHeaderViewInfoList.get(i);
            if (info.view == v) {
                return true;
            }
        }
        return false;
    }

    public void setHeaderVisibility(boolean shouldShow) {
        for (FixedViewInfo fixedViewInfo : mHeaderViewInfoList) {
            fixedViewInfo.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }

    public void setFooterVisibility(boolean shouldShow) {
        for (FixedViewInfo fixedViewInfo : mFooterViewInfoList) {
            fixedViewInfo.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }

    public int getHeadersCount() {
        return mHeaderViewInfoList.size();
    }

    public int getFootersCount() {
        return mFooterViewInfoList.size();
    }

    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderViewInfoList.size();
    }

    private boolean isFooterPosition(int position) {
        return position >= mHeaderViewInfoList.size() + getDataCount();
    }

    private boolean isHeaderViewType(int viewType) {
        return viewType >= BASE_HEADER_VIEW_TYPE
                && viewType < (BASE_HEADER_VIEW_TYPE + mHeaderViewInfoList.size());
    }

    private boolean isFooterViewType(int viewType) {
        return viewType >= BASE_FOOTER_VIEW_TYPE
                && viewType < (BASE_FOOTER_VIEW_TYPE + mFooterViewInfoList.size());
    }

    private BaseHolder createHeaderFooterViewHolder(View view) {
        if (mIsStaggeredGrid) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
        } else {
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }

        return new BaseHolder(view) {
            @Override
            public void setData(Object data) {

            }
        };
    }

    //adv  begin----
    private final List<FixedViewInfo> mAdvViewInfoList = new ArrayList<>();
    private static final int BASE_ADV_VIEW_TYPE = -1 << 12;

    public void addAdvView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_ADV_VIEW_TYPE + mAdvViewInfoList.size();
        mAdvViewInfoList.add(info);
        notifyDataSetChanged();
    }

    public void setAdvVisibility(boolean shouldShow) {
        for (FixedViewInfo fvi : mAdvViewInfoList) {
            fvi.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }

    //adv end-----

    public void setmIsStaggeredGrid(boolean isStaggeredGrid) {
        mIsStaggeredGrid = isStaggeredGrid;
    }

    /**
     * 填充数据,此方法会清空以前的数据
     *
     * @param list 需要显示的数据
     */
    public void fillList(List<D> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 更新数据
     *
     * @param holder item对应的holder
     * @param d      item的数据
     */
    public void updateItem(T holder, D d) {
        data.set(holder.getLayoutPosition(), d);
        notifyItemChanged(holder.getLayoutPosition());
    }

    /**
     * 获取一条数据
     *
     * @param holder item对应的holder
     * @return 该item对应的数据
     */
    public D getItem(T holder) {
        return data.get(holder.getLayoutPosition());
    }

    /**
     * 获取一条数据
     *
     * @param position item的位置
     * @return item对应的数据
     */
    public D getItem(int position) {
        return data.get(position);
    }

    /**
     * 追加一条数据
     *
     * @param d 追加的数据
     */
    public void appendItem(D d) {
        data.add(d);
        notifyItemRangeChanged(data.size() - 2, 1);
    }

    /**
     * 追加一个集合数据
     *
     * @param list 要追加的数据集合
     */
    public void appendList(List<D> list) {
        int start = data.size();
        int count = list.size();
        data.addAll(list);
        notifyItemRangeChanged(start - 1, count);
    }

    /**
     * 在最顶部前置数据
     *
     * @param d 要前置的数据
     */
    public void preposeItem(D d) {
        data.add(0, d);
        notifyItemChanged(0);
    }

    /**
     * 在顶部前置数据集合
     *
     * @param list 要前置的数据集合
     */
    public void preposeList(List<D> list) {
        data.addAll(0, list);
        notifyItemRangeChanged(0, list.size());
    }

    public List<D> getData(){
        return data;
    }
}
