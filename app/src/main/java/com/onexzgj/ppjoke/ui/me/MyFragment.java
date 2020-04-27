package com.onexzgj.ppjoke.ui.me;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mooc.libcommon.utils.StatusBar;
import com.mooc.libnavannotation.FragmentDestination;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.User;
import com.onexzgj.ppjoke.ui.login.UserManager;
import com.onexzgj.ppjoke.ui.me.detail.ProfileActivity;
import com.onexzgj.ppjoke.utils.StringConvert;
import com.onexzgj.ppjoke.view.PPImageView;

@FragmentDestination(pageUrl = "main/tabs/my", needLogin = true)
public class MyFragment extends Fragment implements View.OnClickListener {

    private PPImageView ppMyBackground;
    private PPImageView ppMyAvator;
    private TextView tvMyFans;
    private TextView tvMyLike;
    private TextView tvMyFollow;
    private TextView tvMyScore;
    private View tvMyFeed;
    private View tvMyComment;
    private View tvMyCollect;
    private View tvMyHistory;

    public static final String TAB_TYPE_ALL = "tab_all";
    public static final String TAB_TYPE_FEED = "tab_feed";
    public static final String TAB_TYPE_COMMENT = "tab_comment";

    public static final String KEY_TAB_TYPE = "key_tab_type";
    private View ivMfAll;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ppMyBackground = view.findViewById(R.id.pp_mf_background);
        ppMyAvator = view.findViewById(R.id.pp_mf_avator);
        tvMyFans = view.findViewById(R.id.tv_mf_fans);
        tvMyLike = view.findViewById(R.id.tv_mf_like);
        tvMyFollow = view.findViewById(R.id.tv_mf_follow);
        tvMyScore = view.findViewById(R.id.tv_mf_score);

        tvMyFeed = view.findViewById(R.id.tv_mf_feed);
        tvMyFeed.setOnClickListener(this);

        ivMfAll = view.findViewById(R.id.iv_mf_all);
        ivMfAll.setOnClickListener(this);

        tvMyComment = view.findViewById(R.id.tv_mf_comment);
        tvMyComment.setOnClickListener(this);

        tvMyCollect = view.findViewById(R.id.tv_mf_collect);
        tvMyCollect.setOnClickListener(this);

        tvMyHistory = view.findViewById(R.id.tv_mf_history);
        tvMyHistory.setOnClickListener(this);

        initData();


    }

    private void initData() {
        User user = UserManager.get().getUser();
        if (user != null) {
            tvMyLike.setText(StringConvert.convertSpannable(user.likeCount, getContext().getString(R.string.like_count)));
            tvMyFans.setText(StringConvert.convertSpannable(user.followerCount, getContext().getString(R.string.fans_count)));
            tvMyFollow.setText(StringConvert.convertSpannable(user.likeCount, getContext().getString(R.string.follow_count)));
            tvMyScore.setText(StringConvert.convertSpannable(user.likeCount, getContext().getString(R.string.score_count)));

            PPImageView.setImageUrl(ppMyAvator, user.avatar, true);
            PPImageView.setBlurImageUrl(ppMyBackground,user.avatar,50);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mf_feed:
                ProfileActivity.startProfieActivity(getContext(), TAB_TYPE_FEED);
                break;
            case R.id.tv_mf_comment:
                ProfileActivity.startProfieActivity(getContext(), TAB_TYPE_COMMENT);
                break;
            case R.id.tv_mf_collect:

                break;
            case R.id.tv_mf_history:

                break;
            case R.id.iv_mf_all:
                ProfileActivity.startProfieActivity(getContext(), TAB_TYPE_ALL);
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.lightStatusBar(getActivity(), false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        StatusBar.lightStatusBar(getActivity(), hidden);
    }

}
