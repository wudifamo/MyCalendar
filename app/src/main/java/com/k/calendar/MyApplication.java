package com.k.calendar;

import android.app.Application;

import io.realm.Realm;


/**
 * @author k
 * @date 2018/4/18
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Realm.init(this);
    }

}
