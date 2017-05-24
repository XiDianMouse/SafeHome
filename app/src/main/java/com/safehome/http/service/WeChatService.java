package com.safehome.http.service;


import com.safehome.bean.wechat.WXHttpResponse;
import com.safehome.bean.wechat.WXItemBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by quantan.liu on 2017/3/30.
 */

public interface WeChatService {

    String API_WeChat = "http://api.tianapi.com/";

    /**
     * 微信精选列表
     */
    @GET("wxnew")
    Observable<WXHttpResponse<List<WXItemBean>>> getWXHot(@Query("key") String key, @Query("num") int num, @Query("page") int page);

    /**
     * 微信精选列表
     */
    @GET("wxnew")
    Observable<WXHttpResponse<List<WXItemBean>>> getWXHotSearch(@Query("key") String key, @Query("num") int num, @Query("page") int page, @Query("word") String word);

    /**
     *     https://api.tianapi.com/meinv/?key=APIKEY&num=10
     *     获取广告图片
     */



}
