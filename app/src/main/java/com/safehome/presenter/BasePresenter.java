package com.safehome.presenter;

import com.safehome.app.AppConstants;
import com.safehome.http.LifeSubscription;
import com.safehome.http.Stateful;
import com.safehome.http.utils.Callback;
import com.safehome.http.utils.HttpUtils;

import java.util.List;

import rx.Observable;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class BasePresenter <T extends BaseView>{
    protected T mView;//指的是界面,也就是BaseFragment或者BaseActivity
    private Callback callback;

    public void attachView(LifeSubscription mLifeSubscription){
        this.mView = (T)mLifeSubscription;
    }

    protected <T> void invoke(Observable observable,Callback<T> callback){
        this.callback = callback;
        HttpUtils.invoke((LifeSubscription)mView,observable,callback);
    }

    public void checkState(List list){
        if(list.size()==0){
            if(mView instanceof Stateful){
                ((Stateful)mView).setState(AppConstants.STATE_EMPTY);
            }
            return;
        }
    }

    public void detachView(){
        if(mView!=null){
            mView=null;
        }
        if(callback!=null){
            callback.detachView();
        }
    }
}
