package com.safehome.http.utils;

import com.safehome.bean.gankio.GankIoDataBean;
import com.safehome.bean.gankio.GankIoDayBean;
import com.safehome.http.service.GankIoService;

import rx.Observable;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class RetrofitGankIoUtils extends HttpUtils{
    private GankIoService mGankIoService;

    public RetrofitGankIoUtils(GankIoService gankIoService){
        this.mGankIoService = gankIoService;
    }

    public Observable<GankIoDayBean> fetchGankIoDay(String year, String month, String day){
        return mGankIoService.getGankIoDay(year,month,day);
    }

    public Observable<GankIoDataBean> fetchGankIoData(String id,int page,int pre_page){
        return mGankIoService.getGankIoData(id,page,pre_page);
    }
}
