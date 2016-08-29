package com.zhimeng.base.base;

import java.util.HashMap;

/**
 * author:rongxianzhuo create at 2016/8/16
 * email: rongxianzhuo@gmail.com
 */
public class BaseContext {

    public static final String START_ACTIVITY_KEY = "ZHIMENG_START_ACTIVITY_KEY_154";

    public interface OnResultListener {
        void onResult(Object o);
    }

    public static HashMap<String, Object> requestData = new HashMap<>();
    public static HashMap<String, Object> resultData = new HashMap<>();
    public static HashMap<String, OnResultListener> resultListener = new HashMap<>();

}
