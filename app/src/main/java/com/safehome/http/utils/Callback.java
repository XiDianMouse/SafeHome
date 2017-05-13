package com.safehome.http.utils;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.app.AppConstants;
import com.safehome.http.Stateful;
import com.safehome.presenter.BaseView;

import rx.Subscriber;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class Callback<T> extends Subscriber<T>{
    private Stateful target;

    public void setTarget(Stateful target){
        this.target = target;
    }

    public void detachView(){
        if(target!=null){
            target = null;
        }
    }

    @Override
    public void onCompleted(){

    }

    @Override
    public void onError(Throwable e){
        e.printStackTrace();
        onfail();
    }

    @Override
    public void onNext(T data){
        target.setState(AppConstants.STATE_SUCCESS);
        onResponse();
        onResponse(data);
    }

    public void onResponse(T data){
        ((BaseView)target).refreshView(data);
    }

    public void onResponse(){

    }

    public void onfail(){
        if(!NetworkUtils.isAvailableByPing()){
            ToastUtils.showShortToast("你的网络连接有问题");
            if(target!=null){
                target.setState(AppConstants.STATE_ERROR);
            }
            return;
        }
        ToastUtils.showShortToast("程序员哥哥偷懒去了，快去举报他");
        if (target != null) {
            target.setState(AppConstants.STATE_EMPTY);
        }
    }
}
