package com.safehome.presenter.impl;

import com.safehome.bean.gankio.GankIoDataBean;
import com.safehome.http.utils.Callback;
import com.safehome.http.utils.RetrofitGankIoUtils;
import com.safehome.presenter.BasePresenter;
import com.safehome.presenter.GankIoAndroidPresenter;

import java.util.List;

import javax.inject.Inject;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class GankIoAndroidPresenterImpl extends BasePresenter<GankIoAndroidPresenter.View> implements GankIoAndroidPresenter.Presenter{
    private RetrofitGankIoUtils mRetrofitGankIoUtils;

    @Inject
    public GankIoAndroidPresenterImpl(RetrofitGankIoUtils retrofitGankIoUtils){
        this.mRetrofitGankIoUtils = retrofitGankIoUtils;
    }

    @Override
    public void fetchGankIoData(int page,int pre_page){
        invoke(mRetrofitGankIoUtils.fetchGankIoData("Android",page,pre_page),
            new Callback<GankIoDataBean>(){
                @Override
                public void onResponse(GankIoDataBean data){
                    List<GankIoDataBean.ResultBean> results = data.getResults();
                    checkState(results);
                    mView.refreshView(results);
                }
            }
        );
    }
}
