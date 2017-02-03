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
import com.facebook.drawee.view.SimpleDraweeView;
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
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        final Uri photoURI = FileProvider.getUriForFile(this, "com.zhimeng.test.provider", file);
        /*
        FileSelectActivity.startActivity(this, new String[]{".png", ".jpg"}, new BaseContext.OnResultListener() {
            @Override
            public void onResult(Object o) {
                if (o != null && o instanceof File) toast(((File) o).getName());
            }
        });*/

        CropImageActivity.startActivity(this, photoURI, file.getAbsolutePath(), new BaseContext.OnResultListener() {
            @Override
            public void onResult(Object o) {
                if (o == null) return;
                ((SimpleDraweeView) findViewById(R.id.image_view)).setImageURI(photoURI);
            }
        });
    }
}
