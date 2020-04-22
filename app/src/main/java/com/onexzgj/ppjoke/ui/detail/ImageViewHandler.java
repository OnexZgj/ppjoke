package com.onexzgj.ppjoke.ui.detail;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.ui.InteractionPresenter;
import com.onexzgj.ppjoke.ui.home.ToggleCallback;
import com.onexzgj.ppjoke.utils.TimeUtils;
import com.onexzgj.ppjoke.view.PPImageView;

public class ImageViewHandler extends ViewHandler implements View.OnClickListener {

    private LayoutInflater mInflate;
    private PPImageView headerImage;
    private PPImageView ivDhAuthorAvatar;
    private TextView tvDhAuthorName;
    private TextView tvDhCreateTime;
    private FrameLayout mFlAfdtiTitleContainer;
    private MaterialButton btnDhFollow;
    private TextView mTvFlAfdtiTitle;
    private View titleAuthorInfoLayout;
    private LinearLayout headerViewContainer;
    private PPImageView titleAuthorAvatar;
    private TextView titleAuthorUsername;
    private MaterialButton titleAuthorFollow;
    private TextView titleCreateTime;

    private LinearLayout bottomCollect;
    private LinearLayout bottomLike;
    private LinearLayout bottomShare;
    private AppCompatImageView ivBottomCollect;
    private AppCompatImageView ivBottomLike;
    private TextView tvBottomCollect;
    private TextView tvBottomLikeCount;
    private MaterialButton btnAilFollow;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);
        activity.setContentView(R.layout.activity_feed_detail_type_image);
        mRecyclerView = activity.findViewById(R.id.rv_afdti_commend);
        mTvFlAfdtiTitle = activity.findViewById(R.id.tv_afdti_title);
        mFlAfdtiTitleContainer = activity.findViewById(R.id.fl_afdti_title_container);
        titleAuthorInfoLayout = activity.findViewById(R.id.author_info_layout);
        titleAuthorAvatar = activity.findViewById(R.id.ppiv_author_avatar);
        titleAuthorUsername = activity.findViewById(R.id.tv_author_username);
        titleAuthorFollow = activity.findViewById(R.id.btn_ail_follow);
        titleCreateTime = activity.findViewById(R.id.tv_create_time);
        ivBack = activity.findViewById(R.id.iv_afdti_back);

        inputView = activity.findViewById(R.id.bottom_input_view);
        bottomCollect = activity.findViewById(R.id.bottom_collect);
        bottomLike = activity.findViewById(R.id.bottom_like);
        bottomShare = activity.findViewById(R.id.bottom_share);
        ivBottomCollect = activity.findViewById(R.id.iv_bottom_collect);
        ivBottomLike = activity.findViewById(R.id.iv_bottom_like);
        tvBottomCollect = activity.findViewById(R.id.tv_bottom_collect);
        tvBottomLikeCount = activity.findViewById(R.id.tv_bottom_like_count);
        btnAilFollow = activity.findViewById(R.id.btn_ail_follow);


        mInflate = LayoutInflater.from(activity);

    }

    @Override
    public void bindData(Feed feed) {
        super.bindData(feed);

        bottomShare.setOnClickListener(this);
        bottomLike.setOnClickListener(this);
        bottomCollect.setOnClickListener(this);
        btnAilFollow.setOnClickListener(this);

        tvBottomCollect.setText(feed.ugc.hasFavorite ? "已收藏" : "收藏");
        tvBottomLikeCount.setText("" + feed.ugc.likeCount);

        //title信息绑定
        titleCreateTime.setText(TimeUtils.calculate(feed.createTime));
        PPImageView.setImageUrl(titleAuthorAvatar, feed.author.avatar, true);
        titleAuthorUsername.setText(feed.author.name);
        titleAuthorFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "点击了", Toast.LENGTH_SHORT).show();
            }
        });


        View headView = mInflate.inflate(R.layout.layout_feed_detail_type_image_header, mRecyclerView, false);

        headerViewContainer = headView.findViewById(R.id.header_view_container);
        //头部信息

        ivDhAuthorAvatar = headView.findViewById(R.id.iv_dh_author_avatar);
        tvDhAuthorName = headView.findViewById(R.id.tv_dh_author_name);
        tvDhCreateTime = headView.findViewById(R.id.tv_dh_create_time);
        btnDhFollow = headView.findViewById(R.id.btn_dh_follow);
        PPImageView.setImageUrl(ivDhAuthorAvatar, feed.author.avatar, true);
        tvDhAuthorName.setText(feed.author.name);
        btnDhFollow.setText(feed.author.hasFollow ? mActivity.getText(R.string.has_follow) : mActivity.getText(R.string.unfollow));
        tvDhCreateTime.setText(TimeUtils.calculate(feed.createTime));

        //文本信息
        TextView tvFeedText = headView.findViewById(R.id.tv_feed_text);
        tvFeedText.setText(feed.feeds_text);

        //图片信息
        headerImage = headView.findViewById(R.id.header_image);
        headerImage.bindData(mFeed.width, mFeed.height, mFeed.width > mFeed.height ? 0 : 16, mFeed.cover);

        mAdapter.addHeaderView(headView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean visible = headerViewContainer.getTop() <= -mFlAfdtiTitleContainer.getMeasuredHeight();
                titleAuthorInfoLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
                mTvFlAfdtiTitle.setVisibility(visible ? View.GONE : View.VISIBLE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_share:

                break;
            case R.id.bottom_like:
                InteractionPresenter.toggleFeedLike(mActivity, mFeed, new ToggleCallback() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void toggleSuccess(Feed feed) {
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                tvBottomLikeCount.setText("" + feed.ugc.likeCount);
                                if (feed.ugc.hasLiked) {
                                    ivBottomLike.setBackground(mActivity.getDrawable(R.drawable.icon_cell_liked));
                                }else{
                                    ivBottomLike.setBackground(mActivity.getDrawable(R.drawable.icon_cell_like));
                                }

                                if (feed.ugc.hasFavorite) {
                                    tvBottomCollect.setText(mActivity.getString(R.string.has_collect));
                                    ivBottomCollect.setBackground(mActivity.getDrawable(R.drawable.ic_collected));
                                }else{
                                    tvBottomCollect.setText(mActivity.getString(R.string.collect));
                                    ivBottomCollect.setBackground(mActivity.getDrawable(R.drawable.ic_collect));
                                }
                            }
                        });
                    }

                    @Override
                    public void toggleFail(String message) {

                    }
                });
                break;

            case R.id.bottom_collect:
                InteractionPresenter.toggleFeedFavorite(mActivity,mFeed);
                break;

            case R.id.btn_ail_follow:

                break;
        }
    }
}
