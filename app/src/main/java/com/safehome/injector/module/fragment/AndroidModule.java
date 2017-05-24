package com.safehome.injector.module.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.safehome.adapter.GankIoAndroidAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

/*
* @Module:Module类里面的方法专门提供依赖，所以我们定义一个类，用@Module注解，
* 这样Dagger在构造类的实例的时候，就知道从哪里去找到需要的依赖。
* */
@Module
public class AndroidModule {
    private Context mContext;

    public AndroidModule(Context context){
        mContext = context;
    }

    /*
    * @Provides:在Module中，我们定义的方法是用这个注解，以此来告诉Dagger我们想要构造对象并提供这些依赖。
    * @Singleton:创建某些对象有时候是耗时、浪费资源的或者需要确保其唯一性，这时就需要使用@Singleton注解标注为单例
    * */
    @Provides
    @Singleton
    public RecyclerView.Adapter provideAdapter() {
        return new GankIoAndroidAdapter(mContext);
    }
}
