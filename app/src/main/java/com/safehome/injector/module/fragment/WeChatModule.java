package com.safehome.injector.module.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.safehome.adapter.WeChatAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by quantan.liu on 2017/4/8.
 */
@Module
public class WeChatModule {
    private Context mContext;

    public WeChatModule(Context context){
        mContext = context;
    }

    @Provides
    @Singleton
    public RecyclerView.Adapter provideAdapter() {
        return new WeChatAdapter(mContext);
    }
}
