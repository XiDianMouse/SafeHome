package com.safehome.app;

import android.app.Application;

import com.blankj.utilcode.utils.Utils;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class MyApp extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        //一个utils库的初始化 https://github.com/Blankj/AndroidUtilCode/blob/master/README-CN.
        Utils.init(this);
    }

}
