package com.onexzgj.ppjoke.ui.me.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
/**
 * MyFragment中的详情页
 * @author OnexZgj
 */
import com.google.android.material.tabs.TabLayout;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.User;
import com.onexzgj.ppjoke.ui.login.UserManager;
import com.onexzgj.ppjoke.utils.StringConvert;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAB_TYPE_ALL = "tab_all";
    public static final String TAB_TYPE_FEED = "tab_feed";
    public static final String TAB_TYPE_COMMENT = "tab_comment";

    public static final String KEY_TAB_TYPE = "key_tab_type";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public static void startProfieActivity(Context context, String tabType) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_TAB_TYPE, tabType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User user = UserManager.get().getUser();

        TextView tvLikeCount = findViewById(R.id.like_count);
        TextView tvFansCount = findViewById(R.id.fans_count);
        TextView tvFollowCount = findViewById(R.id.follow_count);
        TextView tvScoreCount = findViewById(R.id.score_count);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        if (user != null) {
            tvLikeCount.setText(StringConvert.convertSpannable(user.likeCount, getString(R.string.like_count)));
            tvFansCount.setText(StringConvert.convertSpannable(user.followerCount, getString(R.string.fans_count)));
            tvFollowCount.setText(StringConvert.convertSpannable(user.likeCount, getString(R.string.follow_count)));
            tvScoreCount.setText(StringConvert.convertSpannable(user.likeCount, getString(R.string.score_count)));
        }

        initData();
    }

    private void initData() {
        String[] tabs = getResources().getStringArray(R.array.profile_tabs);

        viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return ProfileListFragment.newInstance(getTabTypeByPosition(position));
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });


    }

    private String getTabTypeByPosition(int position) {
        switch (position) {
            case 0:
                return TAB_TYPE_ALL;
            case 1:
                return TAB_TYPE_FEED;
            case 2:
                return TAB_TYPE_COMMENT;
        }
        return TAB_TYPE_ALL;
    }
}
