package com.onexzgj.ppjoke.ui.find;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mooc.libnavannotation.FragmentDestination;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.model.SofaTab;
import com.onexzgj.ppjoke.utils.AppConfig;

import java.util.ArrayList;
import java.util.HashMap;

@FragmentDestination(pageUrl = "main/tabs/find")
public class FindFragment extends Fragment {


    private ViewPager2 vpFindViewpage;
    private TabLayout tbFindTab;
    private ArrayList<SofaTab.Tabs> tabs = new ArrayList<>();
    private HashMap<Integer, Fragment> mFragmentMap = new HashMap<>();
    private TabLayoutMediator mediator;
    private SofaTab findConfigs;

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.find_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tbFindTab = view.findViewById(R.id.tb_find_tab);
        vpFindViewpage = view.findViewById(R.id.vp_find_viewpage);

        findConfigs = getFindConfigs();
        for (SofaTab.Tabs tab : findConfigs.tabs) {
            if (tab.enable) {
                tabs.add(tab);
            }
        }

        vpFindViewpage.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);

        vpFindViewpage.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = mFragmentMap.get(position);
                if (fragment == null) {
                    fragment = getTagFragment(position);
                    mFragmentMap.put(position, fragment);
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return tabs.size();
            }
        });

        mediator = new TabLayoutMediator(tbFindTab, vpFindViewpage, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(makeTabView(position));
            }
        });

        mediator.attach();
        vpFindViewpage.registerOnPageChangeCallback(mPageChangeCallback);
    }

    ViewPager2.OnPageChangeCallback mPageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            int tabCount = tbFindTab.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tbFindTab.getTabAt(i);
                TextView customView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    customView.setTextSize(findConfigs.activeSize);
                    customView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    customView.setTextSize(findConfigs.normalSize);
                    customView.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    };

    private View makeTabView(int position) {
        TextView tabView = new TextView(getContext());
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.parseColor(findConfigs.activeColor), Color.parseColor(findConfigs.normalColor)};
        ColorStateList stateList = new ColorStateList(states, colors);
        tabView.setTextColor(stateList);
        tabView.setText(tabs.get(position).title);
        tabView.setTextSize(findConfigs.normalSize);
        return tabView;
    }

    /**
     * 生成相关的Fragment
     *
     * @param position
     * @return
     */
    private Fragment getTagFragment(int position) {
        return TagListFragment.newInstance(tabs.get(position).tag);
    }


    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        String tagType = childFragment.getArguments().getString(TagListFragment.KEY_TAG_TYPE);
        if (TextUtils.equals(tagType, "onlyFollow")) {
            ViewModelProviders.of(childFragment).get(TagListViewModel.class)
                    .getSwitchTabLiveData().observe(this, new Observer() {
                @Override
                public void onChanged(Object o) {
                    vpFindViewpage.setCurrentItem(1);
                }
            });
        }

    }

    /**
     * 通过读取配置文件，将configs进行
     *
     * @return
     */
    private SofaTab getFindConfigs() {
        return AppConfig.getFindTabConfig();
    }
}
