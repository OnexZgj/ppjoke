package com.onexzgj.ppjoke.ui.me.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.onexzgj.ppjoke.base.BaseAdapter;
import com.onexzgj.ppjoke.base.BaseFragment;
import com.onexzgj.ppjoke.exoplayer.PageListPlayDetector;
import com.onexzgj.ppjoke.exoplayer.PageListPlayManager;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.ui.home.HomeAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

public class ProfileListFragment extends BaseFragment<Feed,ProfileViewModle> {

    private String tabType;
    private PageListPlayDetector playDetector;
    private boolean shouldPause = true;
    private HomeAdapter homeAdapter;

    public static ProfileListFragment newInstance(String tabType) {

        Bundle args = new Bundle();
        args.putString(ProfileActivity.KEY_TAB_TYPE, tabType);
        ProfileListFragment fragment = new ProfileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playDetector = new PageListPlayDetector(this, mRecyclerView);
        mViewModel.setProfileType(tabType);
        mSmartRefreshLayout.setEnableRefresh(false);
    }

    @Override
    public PagedListAdapter getAdapter() {
        tabType = getArguments().getString(ProfileActivity.KEY_TAB_TYPE);
        homeAdapter = new HomeAdapter(getContext(), tabType) {
            @Override
            public void onViewDetachedFromWindow2(ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.removeTarget(holder.listPlayerView);
                }
            }

            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.listPlayerView);
                }
            }
        };
        return homeAdapter;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (shouldPause) {
            playDetector.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldPause = true;
        //从评论tab页跳转到 详情页之后再返回回来，咱们需要暂停视频播放。因为评论和tab页是没有视频的
        if (TextUtils.equals(tabType, ProfileActivity.TAB_TYPE_COMMENT)) {
            playDetector.onPause();
        } else {
            playDetector.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        PageListPlayManager.release(tabType);
        super.onDestroyView();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList<Feed> currentList = homeAdapter.getCurrentList();
        finishRefresh(currentList != null && currentList.size() > 0);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}
