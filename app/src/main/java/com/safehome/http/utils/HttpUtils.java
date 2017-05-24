package com.safehome.http.utils;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.app.AppConstants;
import com.safehome.http.LifeSubscription;
import com.safehome.http.Stateful;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class HttpUtils {

    public static <T> void invoke(LifeSubscription lifecycle, Observable<T> observable,Callback<T> callback){
        Stateful target = null;
        if(lifecycle instanceof Stateful){
            target = (Stateful)lifecycle;
            callback.setTarget(target);
        }
        if(!NetworkUtils.isConnected()){
            ToastUtils.showShortToast("网络连接已断开");
            if(target!=null){
                target.setState(AppConstants.STATE_ERROR);
            }
            return;
        }
        Subscription subscription = observable.
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(callback);
        lifecycle.bindSubscription(subscription);
    }
}
