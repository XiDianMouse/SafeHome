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

@Module
public class AndroidModule {
    private Context mContext;

    public AndroidModule(Context context){
        this.mContext = context;
    }

    @Provides
    @Singleton
    public RecyclerView.Adapter provideAdapter() {
        return new GankIoAndroidAdapter();
    }
}
