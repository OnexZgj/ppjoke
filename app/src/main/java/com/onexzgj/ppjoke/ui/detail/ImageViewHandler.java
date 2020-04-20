package com.onexzgj.ppjoke.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.Feed;
import com.onexzgj.ppjoke.view.PPImageView;

public class ImageViewHandler extends ViewHandler {

    private final LayoutInflater mInflate;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);
        activity.setContentView(R.layout.activity_feed_detail_type_image);
        mRecyclerView = activity.findViewById(R.id.rv_afdti_commend);
        ivBack = activity.findViewById(R.id.iv_afdti_back);
        mInflate = LayoutInflater.from(activity);

    }

    @Override
    public void bindData(Feed feed) {
        super.bindData(feed);
        View headView = mInflate.inflate(R.layout.layout_feed_detail_type_image_header,null,false);
        PPImageView headerImage = headView.findViewById(R.id.header_image);
        headerImage.bindData(mFeed.width, mFeed.height, mFeed.width > mFeed.height ? 0 : 16, mFeed.cover);
        mAdapter.addHeaderView(headView);
    }
}
