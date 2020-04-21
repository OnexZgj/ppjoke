package com.onexzgj.ppjoke.ui.detail;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.view.EmptyView;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Comment;
import com.onexzgj.ppjoke.model.Feed;

/**
 * 处理视频和图片的基类
 */
public abstract class ViewHandler {

    protected FeedDetailViewModel viewModel;
    protected RecyclerView mRecyclerView;
    protected ImageView ivBack;

    protected FragmentActivity mActivity;
    protected Feed mFeed;
    protected FeedCommentAdapter mAdapter;

    public ViewHandler(FragmentActivity activity) {
        this.mActivity = activity;
        viewModel = ViewModelProviders.of(activity).get(FeedDetailViewModel.class);
    }


    @CallSuper
    public void bindData(Feed feed) {
        this.mFeed = feed;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);
        mAdapter = new FeedCommentAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);
        viewModel.setItemId(mFeed.itemId);
        viewModel.getPageData().observe(mActivity, new Observer<PagedList<Comment>>() {
            @Override
            public void onChanged(PagedList<Comment> comments) {
                mAdapter.submitList(comments);
                handleEmpty(comments.size() > 0);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

    }

    private EmptyView mEmptyView;

    private void handleEmpty(boolean hasData) {
        if (hasData) {
            if (mEmptyView != null) {
                mAdapter.removeHeaderView(mEmptyView);
            }
        } else {
            if (mEmptyView == null) {
                mEmptyView = new EmptyView(mActivity);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = PixUtils.dp2px(40);
                mEmptyView.setLayoutParams(layoutParams);
                mEmptyView.setTitle(mActivity.getString(R.string.feed_comment_empty));
            }
            mAdapter.addHeaderView(mEmptyView);
        }
    }
}
