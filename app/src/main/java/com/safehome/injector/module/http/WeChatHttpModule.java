package com.safehome.injector.module.http;


import com.safehome.http.service.WeChatService;
import com.safehome.http.utils.RetrofitWeChatUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by quantan.liu on 2017/4/8.
 */
@Module
public class WeChatHttpModule extends BaseHttpModule {
    @Singleton
    @Provides
    Retrofit provideWeChatRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder, client, WeChatService.API_WeChat);
    }

    @Singleton
    @Provides
    WeChatService provideWeChatService(Retrofit retrofit) {
        return retrofit.create(WeChatService.class);
    }
    @Provides
    @Singleton
    RetrofitWeChatUtils provideRetrofitWeChatUtils(WeChatService weChatService) {
        return new RetrofitWeChatUtils(weChatService);
    }
}
