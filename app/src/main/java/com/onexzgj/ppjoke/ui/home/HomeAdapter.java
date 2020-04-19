package com.onexzgj.ppjoke.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mooc.libcommon.view.CornerFrameLayout;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.base.BaseAdapter;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.ui.InteractionPresenter;
import com.onexzgj.ppjoke.view.ListPlayerView;
import com.onexzgj.ppjoke.view.PPImageView;

public class HomeAdapter extends BaseAdapter<Feed, HomeAdapter.ViewHolder> {

    private LayoutInflater inflater;
    protected Context mContext;
    protected String mCategory;

    protected HomeAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });

        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case R.layout.item_feed_image:
                itemView = inflater.inflate(R.layout.item_feed_image, parent, false);
                break;
            case R.layout.item_feed_type_video:
                itemView = inflater.inflate(R.layout.item_feed_type_video, parent, false);
                break;
        }
        return new ViewHolder(itemView, viewType);
    }

    @Override
    protected int getItemViewType2(int position) {
        Feed item = getItem(position);
        if (item.itemType == Feed.TYPE_IMAGE_TEXT) {
            return R.layout.item_feed_image;
        } else if (item.itemType == Feed.TYPE_VIDEO) {
            return R.layout.item_feed_type_video;
        }
        return 0;
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        holder.bindData(getItem(position), position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mItemView;
        int mViewType = 0;
        private ListPlayerView listPlayerView;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.mItemView = itemView;
            this.mViewType = viewType;
        }

        public void bindData(Feed feed, int position) {
            TextView tvFeedText = mItemView.findViewById(R.id.tv_feed_text);

            if (TextUtils.isEmpty(feed.feeds_text)) {
                tvFeedText.setVisibility(View.GONE);
            } else {
                tvFeedText.setVisibility(View.VISIBLE);
                tvFeedText.setText(feed.feeds_text);
            }

            MaterialButton materialButtonFeedTag = mItemView.findViewById(R.id.feed_tag);

            if (TextUtils.isEmpty(feed.activityText)) {
                materialButtonFeedTag.setVisibility(View.GONE);
            } else {
                materialButtonFeedTag.setText(feed.activityText);
                materialButtonFeedTag.setVisibility(View.VISIBLE);
            }

            if (mViewType == R.layout.item_feed_image) {
                PPImageView feedContentImage = mItemView.findViewById(R.id.feed_content_image);
                feedContentImage.bindData(feed.width, feed.height, 16, feed.cover);
            } else if (mViewType == R.layout.item_feed_type_video) {
                listPlayerView = mItemView.findViewById(R.id.list_player_view);
                listPlayerView.bindData(mCategory, feed.width, feed.height, feed.cover, feed.url);
            }


            TextView tvAuthorName = mItemView.findViewById(R.id.tv_author_username);
            PPImageView ivAuthorAvatar = mItemView.findViewById(R.id.iv_author_avatar);

            PPImageView.setImageUrl(ivAuthorAvatar, feed.author.avatar, true);
            tvAuthorName.setText(feed.author.name);

            MaterialButton like = mItemView.findViewById(R.id.like);
            like.setText(feed.ugc.likeCount > 0 ? "" + feed.ugc.likeCount : mContext.getResources().getString(R.string.like));
            like.setTextColor(feed.ugc.hasLiked ? mContext.getResources().getColor(R.color.color_theme) :
                    mContext.getResources().getColor(R.color.color_3d3));
            like.setIconTint(ColorStateList.valueOf(feed.ugc.hasLiked ? mContext.getResources().getColor(R.color.color_theme) :
                    mContext.getResources().getColor(R.color.color_3d3)));
            like.setIcon(feed.ugc.hasLiked ? mContext.getDrawable(R.drawable.icon_cell_liked) :
                    mContext.getDrawable(R.drawable.icon_cell_like));
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InteractionPresenter.toggleFeedLike((LifecycleOwner) mContext, feed, new ToggleCallback() {

                        @SuppressLint("RestrictedApi")
                        @Override
                        public void toggleSuccess() {
                            ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(position);
                                }
                            });
                        }

                        @Override
                        public void toggleFail(String message) {

                        }
                    });
                }
            });


            MaterialButton diss = mItemView.findViewById(R.id.diss);
            diss.setTextColor(feed.ugc.hasdiss ? mContext.getResources().getColor(R.color.color_theme) :
                    mContext.getResources().getColor(R.color.color_3d3));
            diss.setIconTint(ColorStateList.valueOf(feed.ugc.hasdiss ? mContext.getResources().getColor(R.color.color_theme) :
                    mContext.getResources().getColor(R.color.color_3d3)));
            diss.setIcon(feed.ugc.hasdiss ? mContext.getDrawable(R.drawable.icon_cell_dissed) :
                    mContext.getDrawable(R.drawable.icon_cell_diss));
            diss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InteractionPresenter.toggleFeedDiss((LifecycleOwner) mContext, feed, new ToggleCallback() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void toggleSuccess() {
                            ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(position);
                                }
                            });

                        }

                        @Override
                        public void toggleFail(String message) {

                        }
                    });
                }
            });


            MaterialButton comment = mItemView.findViewById(R.id.comment);
            comment.setText("" + feed.ugc.commentCount);
            MaterialButton share = mItemView.findViewById(R.id.share);
            share.setText("" + feed.ugc.shareCount);

            CornerFrameLayout topCommentContainer = mItemView.findViewById(R.id.top_comment_container);
            PPImageView topCommentAvatar = mItemView.findViewById(R.id.top_comment_avatar);
            PPImageView topCommentImage = mItemView.findViewById(R.id.top_comment_coment_image);
            TextView topCommentComment = mItemView.findViewById(R.id.top_comment_comment);
            ImageView topCommentPlay = mItemView.findViewById(R.id.top_comment_play);
            TextView topCommentUserName = mItemView.findViewById(R.id.top_comment_username);
            ImageView topCommentLike = mItemView.findViewById(R.id.top_comment_like);
            TextView topCommentLikeCount = mItemView.findViewById(R.id.top_comment_like_count);
            FrameLayout topCommentImageAudio = mItemView.findViewById(R.id.top_comment_image_audio);

            if (feed.topComment != null) {
                topCommentContainer.setVisibility(View.VISIBLE);
                PPImageView.setImageUrl(topCommentAvatar, feed.topComment.author.avatar, true);
                if (!TextUtils.isEmpty(feed.topComment.imageUrl)) {
                    topCommentImageAudio.setVisibility(View.VISIBLE);
                    topCommentImage.setImageUrl(feed.topComment.imageUrl);
                    if (!TextUtils.isEmpty(feed.topComment.videoUrl)) {
                        topCommentPlay.setVisibility(View.VISIBLE);
                    } else {
                        topCommentPlay.setVisibility(View.GONE);
                    }
                } else {
                    topCommentImageAudio.setVisibility(View.GONE);
                }

                topCommentComment.setText(feed.topComment.commentText);

                topCommentUserName.setText(feed.topComment.author.name);
                topCommentLikeCount.setText("" + feed.topComment.likeCount);

                topCommentLike.setImageDrawable(feed.topComment.hasLiked ? mContext.getDrawable(R.drawable.icon_cell_liked) :
                        mContext.getDrawable(R.drawable.icon_cell_like));

            } else {
                topCommentContainer.setVisibility(View.GONE);
            }

        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }

        public boolean isVideoItem() {
            return mViewType == R.layout.item_feed_type_video;
        }
    }
}
