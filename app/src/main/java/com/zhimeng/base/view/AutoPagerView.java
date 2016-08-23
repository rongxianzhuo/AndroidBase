package com.zhimeng.base.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zhimeng.base.R;

/**
 * author：rongxianzhuo on 2016/8/1 15:44
 * email：rongxianzhuo@gmail.com
 *
 * 若要单独导入功能，需要四个文件，除了这个java文件外，还有一个layout文件，以及另外两个drawable文件，先导入java后根据错误提示自己找啦
 *
 * 自动轮播PagerView，在xml声明后，在代码中调用其setup方法
 */
public class AutoPagerView extends RelativeLayout {

    private class Adapter extends PagerAdapter {

        private View[] views;

        public Adapter(View[] views) {
            this.views = views;
        }

        @Override
        public int getCount() {

            if (views != null) return views.length;
            else return 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views[position]);
            return views[position];
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private Adapter adapter;
    private ViewPager viewPager;
    private LinearLayout circleContainer;
    private View[] circleView;
    private Drawable activeDrawable = getContext().getResources().getDrawable(R.drawable.zhimeng_view_auto_pager_active_circle_bg);
    private Drawable commonDrawable = getContext().getResources().getDrawable(R.drawable.zhimeng_view_auto_pager_circle_bg);

    public AutoPagerView(Context context) {
        super(context);
        init(context);
    }

    public AutoPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init (Context context) {
        circleContainer = new LinearLayout(context);
        circleContainer.setOrientation(LinearLayout.HORIZONTAL);
        viewPager = new ViewPager(context);
        addView(viewPager);
        addView(circleContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        circleContainer.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    /**
     * 初始化
     * @param views 要轮播的视图
     * @param duration 轮播间隔（毫秒）
     */
    public void setup(final View[] views, final int duration) {
        if (views == null || views.length == 0) return;

        activeDrawable = getContext().getResources().getDrawable(R.drawable.zhimeng_view_auto_pager_active_circle_bg);
        commonDrawable = getContext().getResources().getDrawable(R.drawable.zhimeng_view_auto_pager_circle_bg);

        circleView = new View[views.length];
        for (int i = 0; i < views.length; i++) {
            circleView[i] = LayoutInflater.from(getContext()) .inflate(R.layout.zhimeng_view_auto_pager_circle_container, circleContainer, false);
            circleContainer.addView(circleView[i]);
            circleView[i] = circleView[i].findViewById(R.id.auto_pager_view_circle_413);
        }
        circleView[0].setBackground(activeDrawable);

        adapter = new Adapter(views);
        viewPager.setAdapter(adapter);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            public void run() {
                viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % views.length, true);
                handler.postDelayed(this, duration);
            }

        }, duration);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < circleView.length; i++) {
                    if (i == position) circleView[i].setBackground(activeDrawable);
                    else circleView[i].setBackground(commonDrawable);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
