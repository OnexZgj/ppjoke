package com.onexzgj.ppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.base.BaseAdapter;
import com.onexzgj.ppjoke.model.Feed;
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
        View itemView = inflater.inflate(R.layout.item_feed_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    protected int getItemViewType2(int position) {
        Feed item = getItem(position);
        return R.layout.item_feed_image;
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mItemView = itemView;
        }

        public void bindData(Feed item) {
            PPImageView feedImage = mItemView.findViewById(R.id.feed_image);
            feedImage.setImageUrl(item.cover);
        }

    }
}
