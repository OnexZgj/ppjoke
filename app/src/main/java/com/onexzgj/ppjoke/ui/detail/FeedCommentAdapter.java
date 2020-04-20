package com.onexzgj.ppjoke.ui.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mooc.libcommon.extention.AbsPagedListAdapter;
import com.mooc.libcommon.utils.PixUtils;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Comment;
import com.onexzgj.ppjoke.view.PPImageView;

public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {

    protected Context mContext;
    private LayoutInflater mInflater;

    protected FeedCommentAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_feed_comment, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mItemView;
        private TextView tvCommentUserName;
        private PPImageView ppCommentAvator;
        private TextView tvCommentLike;
        private TextView tvComentCreateTime;
        private MaterialButton btnCommentAuthorTag;
        private TextView tvCommentText;
        private PPImageView ivCommentCover;
        private ImageView ivCommentVideoIcon;
        private FrameLayout flCommentExt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mItemView = itemView;
            tvCommentUserName = itemView.findViewById(R.id.tv_comment_username);
            ppCommentAvator = itemView.findViewById(R.id.pp_comment_avatar);
            tvCommentLike = itemView.findViewById(R.id.tv_comment_like);
            tvComentCreateTime = itemView.findViewById(R.id.tv_comment_create_time);
            btnCommentAuthorTag = itemView.findViewById(R.id.btn_comment_author_tag);
            tvCommentText = itemView.findViewById(R.id.tv_comment_text);
            ivCommentCover = itemView.findViewById(R.id.iv_comment_cover);
            ivCommentVideoIcon = itemView.findViewById(R.id.iv_comment_video_icon);
            flCommentExt = itemView.findViewById(R.id.fl_comment_ext);
        }

        public void bindData(Comment item) {
            if (!TextUtils.isEmpty(item.imageUrl)) {
                flCommentExt.setVisibility(View.VISIBLE);
                ivCommentCover.setVisibility(View.VISIBLE);
                ivCommentCover.bindData(item.width, item.height, 0, PixUtils.dp2px(200), PixUtils.dp2px(200), item.imageUrl);
                if (!TextUtils.isEmpty(item.videoUrl)) {
                    ivCommentVideoIcon.setVisibility(View.VISIBLE);
                } else {
                    ivCommentVideoIcon.setVisibility(View.GONE);
                }
            } else {
                ivCommentCover.setVisibility(View.GONE);
                flCommentExt.setVisibility(View.GONE);
                ivCommentVideoIcon.setVisibility(View.GONE);
            }
        }
    }
}
