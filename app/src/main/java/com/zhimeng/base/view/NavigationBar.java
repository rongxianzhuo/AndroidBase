package com.zhimeng.base.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhimeng.base.R;

/**
 * author:rongxianzhuo create at 2016/8/16
 * email: rongxianzhuo@gmail.com
 * 水平导航栏，请记得调用setup方法初始化
 */
public class NavigationBar extends LinearLayout {

    public interface ItemClickListener {
        void click(int position);
    }

    private class ItemHolder {

        public View view;
        public ImageView imageView;
        public TextView textView;
        public @DrawableRes int icon;

        public ItemHolder(Context context, @StringRes int text, @DrawableRes int icon) {
            this.icon = icon;
            view = LayoutInflater.from(context).inflate(R.layout.zhimeng_item_bottom_navigation_bar, null, false);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));
            imageView = (ImageView) view.findViewById(R.id.item_image_1564);
            if (imageView != null) imageView.setImageResource(icon);
            textView = (TextView) view.findViewById(R.id.item_text_1564);
            if (textView != null) textView.setText(text);
        }

        public ItemHolder(Context context, String text, @DrawableRes int icon) {
            this.icon = icon;
            view = LayoutInflater.from(context).inflate(R.layout.zhimeng_item_bottom_navigation_bar, null, false);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));
            imageView = (ImageView) view.findViewById(R.id.item_image_1564);
            if (imageView != null) imageView.setImageResource(icon);
            textView = (TextView) view.findViewById(R.id.item_text_1564);
            if (textView != null) textView.setText(text);
        }

    }

    public NavigationBar(Context context) {
        super(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private ItemHolder[] holders;
    private ItemClickListener listener;

    /**
     * 初始化
     * @param context context
     * @param stringId 标题数组
     * @param iconId 图标数组
     * @param commonTextColor 普通标题颜色
     * @param activeTextColor 激活标题颜色
     * @param commonIconColor 普通图标颜色
     * @param activeIconColor 激活图标颜色
     */
    public void setup(final Context context
            , @StringRes final int[] stringId
            , @DrawableRes final int[] iconId
            , @ColorRes final int commonTextColor
            , @ColorRes final int activeTextColor
            , @ColorRes final int commonIconColor
            , @ColorRes final int activeIconColor) {
        View view = LayoutInflater.from(context).inflate(R.layout.zhimeng_view_navigation_var, this, true);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.navigation_container_8438);
        setBackgroundColor(Color.argb(255, 255, 255, 255));
        setOrientation(HORIZONTAL);
        holders = new ItemHolder[stringId.length];
        for (int i = 0; i < stringId.length; i++) {
            holders[i] = new ItemHolder(context, stringId[i], iconId[i]);
            holders[i].view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < holders.length; j++) {
                        if (view == holders[j].view) {
                            holders[j].imageView.setColorFilter(context.getResources().getColor(activeIconColor));
                            holders[j].textView.setTextColor(context.getResources().getColor(activeTextColor));
                            listener.click(j);
                        }
                        else {
                            holders[j].imageView.setColorFilter(context.getResources().getColor(commonIconColor));
                            holders[j].textView.setTextColor(context.getResources().getColor(commonTextColor));
                        }
                    }
                }
            });
        }
        for (ItemHolder holder : holders) {
            holder.imageView.setColorFilter(context.getResources().getColor(commonIconColor));
            holder.textView.setTextColor(context.getResources().getColor(commonTextColor));
        }
        holders[0].imageView.setColorFilter(context.getResources().getColor(activeIconColor));
        holders[0].textView.setTextColor(context.getResources().getColor(activeTextColor));
        for (ItemHolder h : holders) container.addView(h.view);
    }

    /**
     * 初始化
     * @param context context
     * @param stringId 标题数组
     * @param iconId 未激活图标数组
     * @param activeIconId 激活图标数组
     * @param commonTextColor 普通标题颜色
     * @param activeTextColor 激活标题颜色
     */
    public void setup(final Context context
            , @StringRes final int[] stringId
            , @DrawableRes final int[] iconId
            , @DrawableRes final int[] activeIconId
            , @ColorRes final int commonTextColor
            , @ColorRes final int activeTextColor) {
        View view = LayoutInflater.from(context).inflate(R.layout.zhimeng_view_navigation_var, this, true);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.navigation_container_8438);
        setBackgroundColor(Color.argb(255, 255, 255, 255));
        setOrientation(HORIZONTAL);
        holders = new ItemHolder[stringId.length];
        for (int i = 0; i < stringId.length; i++) {
            holders[i] = new ItemHolder(context, stringId[i], iconId[i]);
            holders[i].view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < holders.length; j++) {
                        if (view == holders[j].view) {
                            holders[j].imageView.setImageResource(activeIconId[j]);
                            holders[j].textView.setTextColor(context.getResources().getColor(activeTextColor));
                            listener.click(j);
                        }
                        else {
                            holders[j].imageView.setImageResource(iconId[j]);
                            holders[j].textView.setTextColor(context.getResources().getColor(commonTextColor));
                        }
                    }
                }
            });
        }
        for (ItemHolder holder : holders) holder.textView.setTextColor(context.getResources().getColor(commonTextColor));
        holders[0].textView.setTextColor(context.getResources().getColor(activeTextColor));
        holders[0].imageView.setImageResource(activeIconId[0]);
        for (ItemHolder h : holders) container.addView(h.view);
    }

    /**
     * item点击事件
     * @param listener 监听者
     */
    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }
}
