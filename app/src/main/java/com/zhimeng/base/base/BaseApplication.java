package com.zhimeng.base.base;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * author:rongxianzhuo create at 2016/8/16
 * email: rongxianzhuo@gmail.com
 */
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
