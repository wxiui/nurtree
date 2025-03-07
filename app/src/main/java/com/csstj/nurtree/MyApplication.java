package com.csstj.nurtree;

import android.app.Application;

import com.csstj.nurtree.common.util.OkHttpUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpUtils.init(this);
    }
}