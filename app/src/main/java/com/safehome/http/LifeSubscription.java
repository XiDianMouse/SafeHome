package com.safehome.http;

import rx.Subscription;

/**
 * @auther gbh
 * Created on 2017/5/5.
 */

public interface LifeSubscription {

    void bindSubscription(Subscription subscription);
}
