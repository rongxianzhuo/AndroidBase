package com.zhimeng.base.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zhimeng.base.R;
import com.zhimeng.base.base.BaseActivity;
import com.zhimeng.base.base.BaseContext;
import com.zhimeng.base.base.BaseFragment;
import com.zhimeng.base.view.pdview.PhotoDraweeView;

/**
 * 浏览大图activity，请调用提供的startActivity静态方法启动
 * 可惜这里使用的 PhotoDraweeView 不是我写的。 原作者：https://github.com/ongakuer/PhotoDraweeView
 */
public class BigImageActivity extends BaseActivity {

    private static String[] urls;
    private static int nowViewPosition = 0;

    private PhotoDraweeView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alphaTitle();
        setContentView(R.layout.zhimeng_activity_big_image);
        imageView = (PhotoDraweeView) findViewById(R.id.zoom_view);
        imageView.setPhotoUri(Uri.parse(urls[nowViewPosition]));
    }

    /**
     * 启动activity
     * @param context context
     * @param urls 图片url
     */
    public static void startActivity(Context context, String[] urls, int beginPosition) {
        BigImageActivity.urls = urls;
        BigImageActivity.nowViewPosition = beginPosition;
        context.startActivity(new Intent(context, BigImageActivity.class));
    }

    private void alphaTitle() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //全屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void previous_click(View view) {
        if (nowViewPosition <= 0) toast(R.string.zhimeng_activity_big_image_first_image);
        else imageView.setPhotoUri(Uri.parse(urls[--nowViewPosition]));
    }

    public void next_click(View view) {
        if (nowViewPosition >= urls.length - 1) toast(R.string.zhimeng_activity_big_image_final_image);
        else imageView.setPhotoUri(Uri.parse(urls[++nowViewPosition]));
    }

    public static int getNowViewPosition() {
        return nowViewPosition;
    }
}
