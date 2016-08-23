package com.zhimeng.base.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zhimeng.base.R;
import com.zhimeng.base.base.BaseActivity;
import com.zhimeng.base.util.BitmapUtil;
import com.zhimeng.base.view.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 编辑图片activity，调用其startActivity方法来启动它
 * 功能：
 * 启动后会根据情况进行拍照或选择本地文件，然后进行图片裁剪，最后将裁减图片保存到指定路径
 *
 * 注意事项：在启动activity前，必需确认app拥有读写权限，否则无法正常工作
 */
public class CropImageActivity extends BaseActivity {

    private static final String IMAGE_PATH_KEY = "image_path";

    public static final String ORDER_TITLE = "title";
    public static final String ORDER_SELECT_FROM_FILE = "file";
    public static final String ORDER_SELECT_FROM_CAMERA = "camera";
    private static final long MAX_PIX = 1000 * 1000;//最大图片像素

    public static final int FROM_FILE = 1314;
    public static final int FROM_CAREMA = 1315;

    private static Bitmap image;//当前图片
    private CropImageView cropImageView;
    private String imageTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        imageTemp = getIntent().getStringExtra(IMAGE_PATH_KEY);
        if (imageTemp == null || imageTemp.isEmpty()) {
            finish();
            return;
        }
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        else toolbar.setVisibility(View.GONE);
        cropImageView = (CropImageView) findViewById(R.id.cropView);
        cropImageView.setHandleColor(getResources().getColor(R.color.colorAccent));
        cropImageView.setFrameColor(getResources().getColor(R.color.colorAccent));
        cropImageView.setGuideColor(getResources().getColor(R.color.colorAccent));
        image = null;
        if (getIntent().getStringExtra(ORDER_TITLE) == null || getIntent().getStringExtra(ORDER_TITLE).equals("")) openCamera();
        else if (getIntent().getStringExtra(ORDER_TITLE).equals(ORDER_SELECT_FROM_FILE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, FROM_FILE);
        }
        else if (getIntent().getStringExtra(ORDER_TITLE).equals(ORDER_SELECT_FROM_CAMERA)) openCamera();
        else openCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.ok) {
            showProgressDialog(R.string.zhimeng_common_message, R.string.zhimeng_activity_crop_waiting_cropping, 5000);
            Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    subscriber.onNext(BitmapUtil.saveBitmap(imageTemp, cropImageView.getCroppedBitmap()));
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean o) {
                    if (o) setResult(RESULT_OK);
                    image.recycle();
                    finish();
                }
            });
        }
        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (image != null && !image.isRecycled()) image.recycle();
        image = null;
        postUiRunnable(new Runnable() {
            @Override
            public void run() {
                showProgressDialog(R.string.zhimeng_common_message, R.string.zhimeng_activity_crop_waiting_reading, 5000);
                Observable.create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        String path = imageTemp;
                        if (requestCode == FROM_FILE) {
                            Uri uri = data.getData();
                            path = BitmapUtil.getRealFilePath(CropImageActivity.this, uri);
                        }
                        image = BitmapUtil.readBitmapByMaxPix(path, MAX_PIX, BitmapUtil.readPictureDegree(path));
                        subscriber.onNext(image);
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap o) {
                        cropImageView.setImageBitmap(o);
                        hideProgressDialog();
                    }
                });
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        File file = new File(imageTemp);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, FROM_CAREMA);
    }

    /**
     * 使用这个方法启动activity
     * @param activity 跳转起始activity
     * @param imagePath 截图完成后图片保存的路径
     * @param fromCamera 是否从摄像机获取图片，默认是从手机中获取图片
     */
    public static void startActivity(Activity activity, String imagePath, boolean fromCamera, int requestCode) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(IMAGE_PATH_KEY, imagePath);
        if (fromCamera) intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_CAMERA);
        else intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_FILE);
        activity.startActivityForResult(intent, requestCode);
    }
}
