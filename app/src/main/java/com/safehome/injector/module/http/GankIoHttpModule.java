package com.safehome.injector.module.http;


import com.safehome.http.service.GankIoService;
import com.safehome.http.utils.RetrofitGankIoUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
/**
 * @auther gbh
 * Created on 2017/5/7.
 */

@Module
public class GankIoHttpModule extends BaseHttpModule {

    /*
    * @Provides:在Module中，我们定义的方法是用这个注解，以此来告诉Dagger我们想要构造对象并提供这些依赖。
    * */
    @Singleton
    @Provides
    Retrofit provideGankIoRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder, client, GankIoService.API_GANKIO);
    }

    @Singleton
    @Provides
    GankIoService provideGankIoService(Retrofit retrofit) {
        return retrofit.create(GankIoService.class);
    }

    @Provides
    @Singleton
    RetrofitGankIoUtils provideRetrofitGankIoUtils(GankIoService gankIoService) {
        return new RetrofitGankIoUtils(gankIoService);
    }
}
