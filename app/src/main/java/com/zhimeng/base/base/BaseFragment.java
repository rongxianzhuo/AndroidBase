package com.zhimeng.base.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;

import java.util.ArrayList;

import rx.Subscriber;

/**
 *
 * Created by rongx on 2016/7/13.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;
    private ArrayList<Runnable> uiUpdateList = new ArrayList<>();
    private boolean isRunningForeground = false;
    private ArrayList<Subscriber<Object>> subscribers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String requestCodeString = "" + requestCode;
        BaseContext.OnResultListener listener = BaseContext.resultListener.get(requestCodeString);
        if (listener != null) listener.onResult(BaseContext.resultData.get(requestCodeString));
        BaseContext.requestData.remove(requestCodeString);
        BaseContext.resultData.remove(requestCodeString);
        BaseContext.resultListener.remove(requestCodeString);
    }

    /**
     * 显示等待会话框
     * @param title 标题
     * @param message 正文消息
     * @param during 最长显示时间（毫秒）
     */
    public void showProgressDialog(@StringRes int title, @StringRes int message, int during) {
        if (progressDialog != null || !isRunningForeground) return;
        progressDialog = ProgressDialog.show(getActivity()
                , getString(title)
                , getString(message)
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
        if (!isRunningForeground || progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunningForeground = true;
        for (int i = 0; i < uiUpdateList.size(); i++) uiUpdateList.get(i).run();
        uiUpdateList.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunningForeground = false;
    }

    /**
     * 为activity增加ui更新操作，如果该操作不能买上执行，则放在activity下次resume后执行
     * @param runnable 更新线程的操作
     */
    public void postUiRunnable(Runnable runnable) {
        if (isRunningForeground) runnable.run();
        else uiUpdateList.add(runnable);
    }

    /**
     * 增加订阅者
     * 调用此方法来为activity订阅可以帮助你在activity销毁时同时取消订阅
     * @param subscriber 订阅者
     */
    public void subscribeRxBus(Subscriber<Object> subscriber) {
        for (Subscriber m : subscribers) if (m == subscriber) return;
        RxBus.toObservable().subscribe(subscriber);
        subscribers.add(subscriber);
    }

    @Override
    public void onDestroy() {
        for (Subscriber m : subscribers) if (m != null && !m.isUnsubscribed()) m.unsubscribe();
        super.onDestroy();
    }
}
