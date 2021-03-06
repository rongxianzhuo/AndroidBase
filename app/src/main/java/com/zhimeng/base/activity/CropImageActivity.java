package com.zhimeng.base.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zhimeng.base.R;
import com.zhimeng.base.base.BaseActivity;
import com.zhimeng.base.base.BaseContext;
import com.zhimeng.base.base.BaseFragment;
import com.zhimeng.base.util.BitmapUtil;
import com.zhimeng.base.view.CropImageView;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 编辑图片activity，调用其startActivity方法来启动它
 * 功能：
 * 启动后会根据情况进行拍照或选择本地文件，然后进行图片裁剪，最后将裁减图片保存到指定路径
 * activity会自动检测并申请文件读写权限
 * 可以在源activity的onActivityResult方法中监听是否成功，成功则resultCode == RESULT_OK
 * 如果源activity是继承我们的BaseActivity或BaseFragment的话，你也可以调用我们自定义的startActivityforResult方法
 */
public class CropImageActivity extends BaseActivity {


    private static final long MAX_PIX = 1000 * 1000;//最大图片像素

    public static final int FROM_CAREMA = 1315;

    private static Uri imageUri;//用于传递给照相机
    private static String realPath;//裁剪照片的实际地址，用于获取图片旋转角度

    private static Bitmap image;//当前图片
    private CropImageView cropImageView;
    private AlertDialog.Builder dialogBuilder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhimeng_activity_crop_image);
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
        showSelectImageDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zhimeng_menu_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.ok) {
            showProgressDialog(R.string.zhimeng_common_message, R.string.zhimeng_activity_crop_waiting_cropping, 5000);
            Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    subscriber.onNext(BitmapUtil.saveBitmap(getContentResolver(), imageUri, cropImageView.getCroppedBitmap()));
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean o) {
                    if (o) setResult("done");
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
        if (requestCode != FROM_CAREMA) return;
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
                        try {
                            image = BitmapUtil.readBitmapByMaxPix(getContentResolver(), imageUri, MAX_PIX, BitmapUtil.readPictureDegree(realPath));
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, FROM_CAREMA);
    }

    /**
     * 使用这个方法启动activity，需要源activity继承BaseActivity，若保存成功，listener回调任意值，包括null
     * @param activity 跳转起始activity
     * @param uri 截图完成后图片保存的路径
     */
    public static void startActivity(BaseActivity activity, Uri uri, String realPath, BaseContext.OnResultListener listener) {
        CropImageActivity.imageUri = uri;
        CropImageActivity.realPath = realPath;
        startActivity(activity, CropImageActivity.class, null, listener);
    }

    /**
     * 使用这个方法启动activity，需要源activity继承BaseActivity，若保存成功，listener回调任意值，包括null
     * @param fragment 跳转起始fragment
     * @param uri 截图完成后图片保存的路径
     */
    public static void startActivity(BaseFragment fragment, Uri uri, String realPath, BaseContext.OnResultListener listener) {
        CropImageActivity.imageUri = uri;
        CropImageActivity.realPath = realPath;
        startActivity(fragment, CropImageActivity.class, null, listener);
    }

    /**
     * 检查并请求获取读写权限
     * @return 是否拥有读写权限
     */
    private boolean checkLocatePermission() {
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean result = checkCallPhonePermission  == PackageManager.PERMISSION_GRANTED;
        if(!result) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}
                    , 0);
        }
        return result;
    }

    private void showSelectImageDialog() {
        if (!checkLocatePermission()) return;
        if (dialogBuilder == null) {
            dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.zhimeng_common_message);
            dialogBuilder.setMessage(R.string.zhimeng_activity_crop_dialog_msg);
            dialogBuilder.setPositiveButton(R.string.zhimeng_activity_crop_dialog_bt1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FileSelectActivity.startActivity(CropImageActivity.this, new String[]{".jpg", ".png"}, new BaseContext.OnResultListener() {
                        @Override
                        public void onResult(final Object o) {
                            if (o == null) {
                                finish();
                                return;
                            }
                            postUiRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    showProgressDialog(R.string.zhimeng_common_message, R.string.zhimeng_activity_crop_waiting_reading, 5000);
                                    Observable.create(new Observable.OnSubscribe<Bitmap>() {
                                        @Override
                                        public void call(Subscriber<? super Bitmap> subscriber) {
                                            try {
                                                image = BitmapUtil.readBitmapByMaxPix(((File) o).getAbsolutePath(), MAX_PIX, BitmapUtil.readPictureDegree(realPath));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                finish();
                                            }
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
                    });
                }
            });
            dialogBuilder.setNegativeButton(R.string.zhimeng_activity_crop_dialog_bt2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openCamera();
                }
            });
            dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            });
        }
        dialogBuilder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkCallPhonePermission  == PackageManager.PERMISSION_GRANTED) showSelectImageDialog();
        else finish();

    }
}
