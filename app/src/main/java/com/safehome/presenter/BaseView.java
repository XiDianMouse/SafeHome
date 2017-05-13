package com.safehome.presenter;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public interface BaseView<T> {
    void refreshView(T mData);//获取数据成功调用该方法
}
