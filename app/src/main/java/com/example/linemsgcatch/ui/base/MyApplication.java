package com.example.linemsgcatch.ui.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;


/**
 * Created by Simon Chang on 2018/09/11.
 */

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    public static Resources getRes() { return mContext.getResources(); }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

}
