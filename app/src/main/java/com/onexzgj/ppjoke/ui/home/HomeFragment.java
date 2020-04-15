package com.onexzgj.ppjoke.ui.home;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;


import com.mooc.libnavannotation.FragmentDestination;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.base.BaseFragment;
import com.onexzgj.ppjoke.base.MutablePageKeyedDataSource;
import com.onexzgj.ppjoke.model.Feed;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends BaseFragment<Feed, HomeViewModel> {

    /**
     * 去详情页的标记
     */
    private String feedType;

    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PagedListAdapter getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new HomeAdapter(getContext(), feedType);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList<Feed> currentList = mAdapter.getCurrentList();
        if (currentList == null || currentList.size() < 0) {
            finishRefresh(true);
            return;
        }
        Feed feed = currentList.get(mAdapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null && data.size() > 0) {
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();
                    //这里要把列表上已经显示的先添加到dataSource.data中
                    //而后把本次分页回来的数据再添加到dataSource.data中
                    dataSource.data.addAll(currentList);
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        Log.d("TAG", "onRefresh:  开始刷新逻辑");
        mViewModel.getDataSource().invalidate();
    }
}
