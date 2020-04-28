package com.onexzgj.ppjoke.ui.me.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
/**
 * MyFragment中的详情页
 * @author OnexZgj
 */
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mooc.libcommon.utils.StatusBar;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.User;
import com.onexzgj.ppjoke.ui.login.UserManager;
import com.onexzgj.ppjoke.utils.StringConvert;
import com.onexzgj.ppjoke.view.PPImageView;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAB_TYPE_ALL = "tab_all";
    public static final String TAB_TYPE_FEED = "tab_feed";
    public static final String TAB_TYPE_COMMENT = "tab_comment";

    public static final String KEY_TAB_TYPE = "key_tab_type";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AppBarLayout appBar;
    private PPImageView topAuthorAvatar;
    private TextView topAuthorName;
    private TextView topAuthorNameLarge;

    public static void startProfieActivity(Context context, String tabType) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_TAB_TYPE, tabType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.fitSystemBar(this);
        setContentView(R.layout.activity_profile);

        User user = UserManager.get().getUser();

        TextView tvLikeCount = findViewById(R.id.like_count);
        TextView tvFansCount = findViewById(R.id.fans_count);
        TextView tvFollowCount = findViewById(R.id.follow_count);
        TextView tvScoreCount = findViewById(R.id.score_count);
        viewPager = findViewById(R.id.view_pager);
        topAuthorAvatar = findViewById(R.id.top_author_avatar);
        topAuthorName = findViewById(R.id.top_author_name);
        topAuthorNameLarge = findViewById(R.id.top_author_name_large);
        topAuthorNameLarge.setText(user.name);
        topAuthorName.setText(user.name);
        PPImageView.setImageUrl(topAuthorAvatar, user.avatar, true);
        appBar = findViewById(R.id.appbar);
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

        viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
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

        new TabLayoutMediator(tabLayout, viewPager, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs[position]);
            }
        }).attach();

        int initTabPosition = getInitTabPosition();
        if (initTabPosition != 0) {
            viewPager.post(() -> {
                viewPager.setCurrentItem(initTabPosition, false);
            });
        }

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                boolean expand = Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange();
                topAuthorAvatar.setVisibility(expand ? View.GONE : View.VISIBLE);
                topAuthorName.setVisibility(expand ? View.GONE : View.VISIBLE);
                topAuthorNameLarge.setVisibility(expand ? View.VISIBLE : View.GONE);
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

    private int getInitTabPosition() {
        String initTab = getIntent().getStringExtra(KEY_TAB_TYPE);

        switch (initTab) {
            case TAB_TYPE_ALL:
                return 0;
            case TAB_TYPE_FEED:
                return 1;
            case TAB_TYPE_COMMENT:
                return 2;
            default:
                return 0;
        }
    }
}
