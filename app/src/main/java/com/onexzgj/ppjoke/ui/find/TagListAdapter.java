package com.onexzgj.ppjoke.ui.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mooc.libcommon.extention.LiveDataBus;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.base.BaseAdapter;
import com.onexzgj.ppjoke.model.TagList;
import com.onexzgj.ppjoke.ui.InteractionPresenter;
import com.onexzgj.ppjoke.ui.find.detail.TagFeedListActivity;
import com.onexzgj.ppjoke.view.PPImageView;

public class TagListAdapter extends BaseAdapter<TagList, TagListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    protected TagListAdapter(Context context) {
        super(new DiffUtil.ItemCallback<TagList>() {
            @Override
            public boolean areItemsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.tagId == newItem.tagId;
            }

            @Override
            public boolean areContentsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_tag_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    protected int getItemViewType2(int position) {
        return R.layout.layout_tag_list_item;
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        TagList item = getItem(position);
        holder.bindData(item, position);

        holder.actionFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractionPresenter.toggleTagFollow(((LifecycleOwner) mContext), item);

                TagListObserver tagListObserver = new TagListObserver();

                LiveDataBus.get().with(InteractionPresenter.DATA_FROM_INTERACTION_FOLLOW)
                        .observe((LifecycleOwner) mContext, tagListObserver);

                tagListObserver.setTagList(item, position);
            }
        });

        holder.itemView.setOnClickListener(v -> TagFeedListActivity.startActivity(mContext, item));

    }

    class TagListObserver implements Observer<TagList> {

        public TagList mTagList;
        public int mPosition = 0;

        @Override
        public void onChanged(TagList tagList) {
            if (mTagList.id != tagList.id)
                return;
            mTagList.hasFollow = tagList.hasFollow;
            notifyItemChanged(mPosition);
        }

        public void setTagList(TagList item, int position) {
            mTagList = item;
            mPosition = position;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton actionFollow;
        private TextView tagDesc;
        private TextView tagTitle;
        private PPImageView tagAvtar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagAvtar = itemView.findViewById(R.id.tag_avtar);
            tagTitle = itemView.findViewById(R.id.tag_title);
            tagDesc = itemView.findViewById(R.id.tag_desc);
            actionFollow = itemView.findViewById(R.id.action_follow);

        }

        public void bindData(TagList item, int position) {
            PPImageView.setImageUrl(tagAvtar, item.icon, false, 8);
            tagTitle.setText(item.title);
            tagDesc.setText(item.feedNum + mContext.getString(R.string.tag_list_item_hot_feed));
            actionFollow.setText(item.hasFollow ? mContext.getString(R.string.has_follow) : mContext.getString(R.string.unfollow));
        }
    }
}
