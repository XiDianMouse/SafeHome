package com.safehome.listeners;

/**
 * @auther gbh
 * Created on 2017/5/22.
 */

public interface OnReceiverListener<T> {
    void onReceive(T data,T description);
}
