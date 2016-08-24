package com.zhimeng.base.view;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zhimeng.base.R;

/**
 * author：rongxianzhuo on 2016/7/27 14:39
 * email：rongxianzhuo@gmail.com
 * 自定义组合View，快速实现TabLayout + ViewPager 效果
 * 使用方法：
 * 1.在xml中加入该View
 * 2.代码中调用其setup方法
 */
public class TabPagerView extends LinearLayout {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private TabPagerView(Context context) {
        super(context);
        init(context);
    }

    public TabPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.zhimeng_view_tab_pager, this, true);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout_18534);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager_18534);
    }

    /**
     * TabPagerView id can not be 'tab_layout' or 'tab_pager'
     * @param activity 所在的activity
     * @param pagerTitles 标题数组
     * @param fragments 碎片数组
     */
    public void setup(AppCompatActivity activity, String[] pagerTitles, Fragment[] fragments){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(activity.getSupportFragmentManager(), pagerTitles, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;
        private String[] titles;
        public SectionsPagerAdapter(FragmentManager fm, String[] titles, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
        @Override
        public int getCount() {
            return fragments.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}