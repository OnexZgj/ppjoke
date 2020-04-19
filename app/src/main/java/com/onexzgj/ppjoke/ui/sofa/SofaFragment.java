package com.onexzgj.ppjoke.ui.sofa;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mooc.libnavannotation.FragmentDestination;
import com.onexzgj.ppjoke.R;
import com.onexzgj.ppjoke.exoplayer.PageListPlayManager;
import com.onexzgj.ppjoke.model.SofaTab;
import com.onexzgj.ppjoke.ui.sofa.item.SofaFragmentItem;
import com.onexzgj.ppjoke.utils.AppConfig;

import java.util.ArrayList;
import java.util.HashMap;

@FragmentDestination(pageUrl = "main/tabs/sofa")
public class SofaFragment extends Fragment {

    private TabLayout tbSftab;
    private ViewPager2 vpSfViewpage;
    private SofaTab tabConfig;
    private ArrayList<SofaTab.Tabs> tabs;
    private HashMap<Integer, Fragment> mFragmentMap = new HashMap<>();
    private TabLayoutMediator mediator;

    public static SofaFragment newInstance() {
        return new SofaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.sofa_fragment, container, false);
        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tbSftab = view.findViewById(R.id.tb_sf_tab);
        vpSfViewpage = view.findViewById(R.id.vp_sf_viewpage);
        tabConfig = getTabConfig();
        tabs = new ArrayList<>();
        for (SofaTab.Tabs tab : tabConfig.tabs) {
            if (tab.enable) {
                tabs.add(tab);
            }
        }

        vpSfViewpage.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        vpSfViewpage.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = mFragmentMap.get(position);
                if (fragment == null) {
                    fragment = getTabFragment(position);
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return tabs.size();
            }
        });

        mediator = new TabLayoutMediator(tbSftab, vpSfViewpage, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(makeTabView(position));
            }
        });

        mediator.attach();
        vpSfViewpage.registerOnPageChangeCallback(mPageChangeCallback);

    }

    ViewPager2.OnPageChangeCallback mPageChangeCallback =new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            int tabCount = tbSftab.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tbSftab.getTabAt(i);
                TextView customView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    customView.setTextSize(tabConfig.activeSize);
                    customView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    customView.setTextSize(tabConfig.normalSize);
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
        int[] colors = new int[]{Color.parseColor(tabConfig.activeColor), Color.parseColor(tabConfig.normalColor)};
        ColorStateList stateList = new ColorStateList(states, colors);
        tabView.setTextColor(stateList);
        tabView.setText(tabs.get(position).title);
        tabView.setTextSize(tabConfig.normalSize);
        return tabView;
    }

    /**
     * 获取tab栏的fragment的情况
     * @param position
     * @return
     */
    private Fragment getTabFragment(int position) {
        return SofaFragmentItem.newInstance(tabs.get(position).tag);
    }

    public SofaTab getTabConfig() {
        return AppConfig.getSofaTabConfig();
    }
}
