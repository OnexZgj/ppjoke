package com.onexzgj.ppjoke.ui.find.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.mooc.libcommon.extention.AbsPagedListAdapter;
import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.utils.StatusBar;
import com.mooc.libcommon.view.EmptyView;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.base.BaseAdapter;
import com.onexzgj.ppjoke.exoplayer.PageListPlayDetector;
import com.onexzgj.ppjoke.exoplayer.PageListPlayManager;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.model.TagList;
import com.onexzgj.ppjoke.ui.home.HomeAdapter;
import com.onexzgj.ppjoke.utils.StringConvert;
import com.onexzgj.ppjoke.view.PPImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class TagFeedListActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    private BaseAdapter adapter;
    public static final String KEY_TAG_LIST = "tag_list";
    public static final String KEY_FEED_TYPE = "tag_feed_list";
    private PageListPlayDetector playDetector;
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private SmartRefreshLayout refreshLayout;
    private ImageView actionBack;
    private TagList tagList;
    private boolean shouldPause = true;
    private TagFeedListViewModel tagFeedListViewModel;
    private int totalScrollY;
    private ConstraintLayout topBar;
    private View topLine;
    private PPImageView tagLogo;
    private MaterialButton topBarFollow;

    public static void startActivity(Context context, TagList tagList) {
        Intent intent = new Intent(context, TagFeedListActivity.class);
        intent.putExtra(KEY_TAG_LIST, tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.fitSystemBar(this);
        setContentView(R.layout.activity_tag_feed_list);
        tagList = (TagList) getIntent().getSerializableExtra(KEY_TAG_LIST);

        recyclerView = findViewById(R.id.rv_lrv_common);
        emptyView = findViewById(R.id.empty_view);
        refreshLayout = findViewById(R.id.refresh_layout);
        actionBack = findViewById(R.id.action_back);
        topBarFollow = findViewById(R.id.top_bar_follow);
        topBar = findViewById(R.id.top_bar);
        tagLogo = findViewById(R.id.tag_logo);
        topLine = findViewById(R.id.top_line);
        actionBack.setOnClickListener(this);

        topBarFollow.setText(tagList.hasFollow ? getString(R.string.has_follow) : getString(R.string.unfollow));

        PPImageView.setImageUrl(tagLogo, tagList.icon, true);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = (BaseAdapter) getAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setAnimation(null);

        tagFeedListViewModel = new ViewModelProvider(this).get(TagFeedListViewModel.class);

        tagFeedListViewModel.setFeedType(tagList.title);
        tagFeedListViewModel.getPageData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });

        playDetector = new PageListPlayDetector(this, recyclerView);
        addHeaderView();
    }

    private void addHeaderView() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_tag_feed_list_header, recyclerView, false);
        TextView headerTitle = headerView.findViewById(R.id.header_title);
        PPImageView headerBackground = headerView.findViewById(R.id.header_background);
        TextView headerFollow = headerView.findViewById(R.id.header_follow);
        TextView headerIntro = headerView.findViewById(R.id.header_intro);
        TextView headerTagWatch = headerView.findViewById(R.id.header_tag_watcher);

        PPImageView.setImageUrl(headerBackground, tagList.background, false);
        headerTitle.setText(tagList.title);
        headerFollow.setText(tagList.hasFollow ? getString(R.string.has_follow) : getString(R.string.unfollow));
        headerIntro.setText(tagList.intro);
        headerTagWatch.setText(StringConvert.convertTagFeedList(tagList.enterNum));
        headerTagWatch.setVisibility(tagList.enterNum > 0 ? View.VISIBLE : View.GONE);

        adapter.addHeader(headerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY += dy;
                boolean overHeight = totalScrollY > PixUtils.dp2px(48);
                tagLogo.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                headerTitle.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                headerFollow.setVisibility(overHeight ? View.VISIBLE : View.GONE);
                actionBack.setImageResource(overHeight ? R.drawable.icon_back_black : R.drawable.icon_back_white);
                topBar.setBackgroundColor(overHeight ? Color.WHITE : Color.TRANSPARENT);
                topLine.setVisibility(overHeight ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }


    private void submitList(PagedList<Feed> feeds) {
        if (feeds.size() > 0) {
            adapter.submitList(feeds);
        }
        finishRefresh(feeds.size() > 0);
    }


    public BaseAdapter getAdapter() {
        return new HomeAdapter(this, KEY_FEED_TYPE) {
            @Override
            public void onViewAttachedToWindow2(@NonNull ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow2(@NonNull ViewHolder holder) {
                playDetector.removeTarget(holder.getListPlayerView());
            }


            public void onStartFeedDetailActivity(Feed feed) {
                boolean isVideo = feed.itemType == Feed.TYPE_VIDEO;
                shouldPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                //这个方法是在我们每提交一次 pagelist对象到adapter 就会触发一次
                //每调用一次 adpater.submitlist
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (shouldPause) {
            playDetector.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shouldPause = true;
        playDetector.onResume();
    }

    @Override
    protected void onDestroy() {
        PageListPlayManager.release(KEY_FEED_TYPE);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        tagFeedListViewModel.getDataSource().invalidate();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList currentList = getAdapter().getCurrentList();
        finishRefresh(currentList != null && currentList.size() > 0);
        //全权委托给pageing框架
    }


    private void finishRefresh(boolean hasData) {
        PagedList currentList = adapter.getCurrentList();
        hasData = currentList != null && currentList.size() > 0 || hasData;

        if (hasData) {
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        RefreshState state = refreshLayout.getState();
        if (state.isOpening && state.isHeader) {
            refreshLayout.finishRefresh();
        } else if (state.isOpening && state.isFooter) {
            refreshLayout.finishLoadMore();
        }
    }

}
