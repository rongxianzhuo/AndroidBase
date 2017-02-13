package com.zhimeng.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhimeng.base.activity.BigImageActivity;
import com.zhimeng.base.activity.CropImageActivity;
import com.zhimeng.base.activity.FileSelectActivity;
import com.zhimeng.base.base.BaseActivity;
import com.zhimeng.base.base.BaseContext;
import com.zhimeng.base.util.BitmapUtil;
import com.zhimeng.base.view.LoginView;
import com.zhimeng.base.view.TabPagerView;

import java.io.File;

public class MainActivity extends BaseActivity {

    private File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/crop.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BigImageActivity.startActivity(this, "http://avatar.csdn.net/8/7/6/1_shlock_fan.jpg");
        //BigImageActivity.startActivity(this, "http://s1.dwstatic.com/group1/M00/D1/58/df2ffd7f8fcbfaab40a080ecee228313.gif");
/*
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri("http://s1.dwstatic.com/group1/M00/D1/58/df2ffd7f8fcbfaab40a080ecee228313.gif")
                .setAutoPlayAnimations(true)
                .build();
        ((SimpleDraweeView) findViewById(R.id.image_view)).setController(controller);*/
    }
}
