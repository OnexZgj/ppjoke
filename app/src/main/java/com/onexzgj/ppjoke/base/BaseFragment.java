package com.onexzgj.ppjoke.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.libcommon.view.EmptyView;
import com.onexzgj.ppjoke.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<T, M extends BaseViewModel<T>> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private SmartRefreshLayout mSmartRefreshLayout;
    public RecyclerView mRecyclerView;
    public EmptyView mEmptyView;

    protected PagedListAdapter<T, RecyclerView.ViewHolder> mAdapter;
    private DividerItemDecoration decoration;
    public M mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_refresh_view, container, false);
        mSmartRefreshLayout = root.findViewById(R.id.refresh_layout);
        mRecyclerView = root.findViewById(R.id.rv_lrv_common);
        mEmptyView = root.findViewById(R.id.empty_view);

        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);

        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        //默认给列表中的Item 一个 10dp的ItemDecoration

        decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(null);

        genericViewModel();

        return root;
    }


    /**
     * 生成ViewModel类
     */
    protected void genericViewModel() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if (arguments.length > 1) {
            Type argument = arguments[1];
            Class modelClaz = ((Class) argument).asSubclass(BaseViewModel.class);
            mViewModel = (M) ViewModelProviders.of(this).get(modelClaz);

            mViewModel.getPageData().observe(getViewLifecycleOwner(), data -> submitList(data));

            mViewModel.getBoundaryPageData().observe(getViewLifecycleOwner(), hasData -> finishRefresh(hasData));
        }
    }

    /**
     * 结束上拉或者下拉刷新的加载逻辑
     *
     * @param hasData 这次请求是否有数据
     */
    public void finishRefresh(Boolean hasData) {
        PagedList<T> currentList = mAdapter.getCurrentList();
        hasData = hasData || currentList != null && currentList.size() > 0;
        RefreshState state = mSmartRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            mSmartRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            mSmartRefreshLayout.finishRefresh();
        }
        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }

    }

    public void submitList(PagedList<T> data) {
        if (data.size() > 0) {
            mAdapter.submitList(data);
        }
        finishRefresh(data.size() > 0);
    }


    /**
     * 传入Adapter
     *
     * @return
     */
    protected abstract PagedListAdapter getAdapter();
}
