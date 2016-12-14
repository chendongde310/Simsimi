package com.chendong.ai.simsimi;

import android.content.Context;

import com.chendong.ai.simsimi.api.SimsimiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/13 - 17:40
 * 注释：
 */
public class MyService {

    private static MyService ourInstance ;
    private Context context;
    private Retrofit retrofit;
    private SimsimiService service;

    private MyService(Context context ) {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.simsimi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(SimsimiService.class);
    }

    public static MyService getInstance() {
        if(ourInstance==null){
            throw new NullPointerException("MyService没有初始化");
        }
        return ourInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static void intiService(Context context ) {
       if(ourInstance==null){
           ourInstance = new MyService(context);
       }else {
           ourInstance.setContext(context);
       }
    }

    public SimsimiService getService() {
        return service;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }



}
