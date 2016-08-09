package com.zhimeng.base.base;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Handler;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 *
 * Created by rongx on 2016/7/13.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;
    private boolean isRunningForeground = false;
    private ArrayList<Runnable> uiUpdateList = new ArrayList<>();

    /**
     * 显示等待会话框
     * @param title 标题
     * @param message 正文消息
     * @param during 最长显示时间（毫秒）
     */
    public void showProgressDialog(String title, String message, int during) {
        progressDialog = ProgressDialog.show(getActivity()
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
        if (progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

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
}
