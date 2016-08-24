package com.zhimeng.base.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * author Xianzhuo Rong
 * time   2016/6/21.
 * email rongxianzhuo@gmail.com
 * github https://github.com/rongxianzhuo
 */
public class BaseActivity extends AppCompatActivity {

    private static final String START_ACTIVITY_FOR_RESULT_KEY = "START_ACTIVITY_FOR_RESULT_KEY_154";

    public interface OnResultListener {
        void onResult(Object o);
    }

    private static ArrayList<Pair<String, Object>> intentData = new ArrayList<>();

    private ArrayList<Pair<String, OnResultListener>> listeners = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ArrayList<Runnable> uiUpdateList = new ArrayList<>();
    private boolean isRunningForeground = false;
    private ArrayList<Subscriber<Object>> subscribers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listeners.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String requestCodeString = "" + requestCode;
        for (int i = 0; i < listeners.size(); i++) if (listeners.get(i).first.equals(requestCodeString)) {
            Object object = null;
            for (int j = 0; j < intentData.size(); j++) if (intentData.get(j).first.equals(requestCodeString)) {
                object = intentData.get(j).second;
                intentData.remove(j);
                break;
            }
            listeners.get(i).second.onResult(object);
            listeners.remove(i);
            break;
        }
    }

    /**
     * 更方便的startActivityForResult（不用监听onActivityResult方法）
     * @param intent intent
     * @param listener listener，获得对方activity返回的Object
     */
    public void startActivityForResult(Intent intent, OnResultListener listener) {
        String stringCode = null;
        for (Pair<String, OnResultListener> pair : listeners) if (pair.second == listener) {
            stringCode = pair.first;
            break;
        }
        if (stringCode == null) {
            int code =  (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
            listeners.add(new Pair<>("" + code, listener));
            intent.putExtra(START_ACTIVITY_FOR_RESULT_KEY, "" + code);
            startActivityForResult(intent, code);
        }
        else {
            try {
                int code = Integer.parseInt(stringCode);
                intent.putExtra(START_ACTIVITY_FOR_RESULT_KEY, stringCode);
                startActivityForResult(intent, code);
            }
            catch (Exception e) {
                Log.e("zhimeng", e.getMessage());
            }
        }
    }

    /**
     * 如果上一个activity是通过startActivityForResult(Intent intent, OnResultListener listener)方法启动当前activity，这通过该方法设置返回结果
     * @param o 结果
     */
    public void setResult(Object o) {
        Intent intent = getIntent();
        if (intent == null) return;
        String requestCode = intent.getStringExtra(START_ACTIVITY_FOR_RESULT_KEY);
        for (Pair<String, OnResultListener> pair : listeners) if (pair.first.equals(requestCode)) {
            intentData.add(new Pair<String, Object>(requestCode, o));
            return;
        }
    }

    /**
     * 显示等待会话框
     * @param title 标题
     * @param message 正文消息
     * @param during 最长显示时间（毫秒）
     */
    public void showProgressDialog(@StringRes int title, @StringRes int message, int during) {
        if (progressDialog != null || isFinishing()) return;
        progressDialog = ProgressDialog.show(this
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
    protected void onDestroy() {
        for (Subscriber m : subscribers) if (m != null && !m.isUnsubscribed()) m.unsubscribe();
        super.onDestroy();
    }
}