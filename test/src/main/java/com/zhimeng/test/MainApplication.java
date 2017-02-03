package com.zhimeng.test;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * author:rongxianzhuo create at 2017/2/2
 * email: rongxianzhuo@gmail.com
 * email: https://github.com/rongxianzhuo
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
