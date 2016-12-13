package com.chendong.ai.simsimi;

import com.chendong.ai.simsimi.api.SimsimiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/13 - 17:40
 * 注释：
 */
public class MyService {
    private static MyService ourInstance = new MyService();
    private Retrofit retrofit;
    private SimsimiService service;

    private MyService() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.simsimi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(SimsimiService.class);
    }

    public static MyService getInstance() {
        return ourInstance;
    }


    public SimsimiService getService() {
        return service;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }





}
