package com.chendong.ai.simsimi;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/14 - 11:44
 * 注释：
 */
public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Hawk.init(this)
//                .setLogInterceptor(new LogInterceptor() {
//                    @Override
//                    public void onLog(String message) {
//                        Logger.d(message);
//                    }
//                })
                .build();
        MyService.intiService(this);
    }
}
