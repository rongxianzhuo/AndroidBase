package com.zhimeng.base.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.zhimeng.base.R;
import com.zhimeng.base.view.pdview.PhotoDraweeView;

/**
 * 浏览大图activity，请调用提供的startActivity静态方法启动
 */
public class BigImageActivity extends AppCompatActivity {

    private static final String URL_KEY = "urls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alphaTitle();
        setContentView(R.layout.zhimeng_activity_big_image);
        String url;
        try {
            url = getIntent().getStringExtra(URL_KEY);
        }
        catch (Exception e) {
            Log.e("qutao", e.getMessage());
            finish();
            return;
        }
        PhotoDraweeView imageView = (PhotoDraweeView) findViewById(R.id.zoom_view);
        imageView.setPhotoUri(Uri.parse(url));
    }

    /**
     * 启动activity
     * @param context context
     * @param urls 图片url
     */
    public static void startActivity(Context context, String urls) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra(URL_KEY, urls);
        context.startActivity(intent);
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
}
