package com.zhimeng.base.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * author Xianzhuo Rong
 * time   2016/6/21.
 * email rongxianzhuo@gmail.com
 * github https://github.com/rongxianzhuo
 */
public class BaseActivity extends AppCompatActivity {

    public interface OnResultListener {
        void onResult(int requestCode, int resultCode, Intent data);
    }

    private ArrayList<OnResultListener> listeners;
    private ProgressDialog progressDialog;
    private ArrayList<Runnable> uiUpdateList = new ArrayList<>();
    private boolean isRunningForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listeners = new ArrayList<>();
    }

    public void addOnResultListener(OnResultListener listener) {
        listeners.add(listener);
    }

    public void removeOnResultListener(OnResultListener listener) {
        listeners.remove(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int i = 0;
        while (i < listeners.size()) {
            listeners.get(i).onResult(requestCode, resultCode, data);
            i++;
        }
    }

    /**
     * 显示等待会话框
     * @param title 标题
     * @param message 正文消息
     * @param during 最长显示时间（毫秒）
     */
    public void showProgressDialog(String title, String message, int during) {
        if (progressDialog != null || isFinishing()) return;
        progressDialog = ProgressDialog.show(this
                , title
                , message
                , false, false);
        final ProgressDialog mProgressDialog = progressDialog;
        new Handler().postDelayed(new Runnable(){

            public void run() {
                if (mProgressDialog == progressDialog) hideProgressDialog();//设置ProgressDialog显示的最长时间
            }

        }, during);
    }

    /**
     * 隐藏使用showProgressDialog方法显示的对话框（如果有的话）
     */
    public void hideProgressDialog() {
        if (isFinishing() || progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunningForeground = true;
        for (int i = 0; i < uiUpdateList.size(); i++) uiUpdateList.get(i).run();
        uiUpdateList.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunningForeground = false;
    }

    @Override
    public void finish() {
        hideProgressDialog();
        super.finish();
    }

    /**
     * 为activity增加ui更新操作，如果该操作不能买上执行，则放在activity下次resume后执行
     * @param runnable 更新线程的操作
     */
    public void postUiRunnable(Runnable runnable) {
        if (isRunningForeground) runnable.run();
        else uiUpdateList.add(runnable);
    }
}