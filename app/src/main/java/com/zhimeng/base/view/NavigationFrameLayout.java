package com.zhimeng.base.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.Arrays;

/**
 * author:rongxianzhuo create at 2016/8/16
 * email: rongxianzhuo@gmail.com
 * 与NavigationBar配合使用的FrameLayout，可以跟随NavigationBar进行切换碎片
 */
public class NavigationFrameLayout extends FrameLayout {

    public NavigationFrameLayout(Context context) {
        super(context);
    }

    public NavigationFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private @IdRes int fragmentLayoutId;
    private int currentPosition = 0;
    private boolean[] added;
    private Fragment[] fragments;

    /**
     * 设置NavigationBar，这里会调用NavigationBar的setItemClickListener，由于目前NavigationBar只支持一个listener，所以不要再其他地方再设置NavigationBar的ItemClickListener了
     * @param activity activity
     * @param fragmentLayoutId NavigationFrameLayout在xml文件中的id
     * @param bar 水平导航栏
     * @param fragments 碎片数组
     */
    public void setupWithNavigation(final Activity activity, @IdRes int fragmentLayoutId, NavigationBar bar, Fragment[] fragments) {
        this.fragmentLayoutId = fragmentLayoutId;
        this.fragments = fragments;
        added = new boolean[fragments.length];
        Arrays.fill(added, false);
        currentPosition = -1;
        showFragment(activity, 0);
        bar.setItemClickListener(new NavigationBar.ItemClickListener() {
            @Override
            public void click(int position) {
                showFragment(activity, position);
            }
        });
    }

    /**
     *
     * @param position 要展示的碎片的编号
     */
    private void showFragment(Activity activity, int position) {
        if (currentPosition == position) return;
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        if (currentPosition >= 0 && currentPosition < fragments.length) fragmentTransaction.hide(fragments[currentPosition]);
        currentPosition = position;
        if (!added[position]) fragmentTransaction.add(fragmentLayoutId, fragments[position]);
        else fragmentTransaction.show(fragments[position]);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
        added[position] = true;
    }
}
