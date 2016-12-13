package com.chendong.ai.simsimi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 作者：陈东  —  www.renwey.com
 * 日期：2016/12/13 - 16:03
 * 注释：
 */
public interface SimsimiService {

    @GET("/getRealtimeReq?uuid=ACNDvRKYFC0aIVlFbo7cTKOv9DOWjUIuhMh70xpWX7i&lc=zh&ft=0.5&status=W")
    Call<Request> getReqText(@Query("reqText")String Text);

    @GET("/getRealtimeReq?uuid=ACNDvRKYFC0aIVlFbo7cTKOv9DOWjUIuhMh70xpWX7i&lc=zh&status=W")
    Call<Request> getReqText(@Query("reqText")String Text,@Query("ft")double ft);






}
