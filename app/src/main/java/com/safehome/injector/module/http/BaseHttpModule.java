package com.safehome.injector.module.http;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */
@Module
public class BaseHttpModule {
    /**
     * 提供http的帮助类 以下部分现在都放在子类。
     * 更换链接的请求，需要添加如AppModule的provideRetrofitZhiHuUtils()方法 命名规则provideRetrofitXXXUtils()
     * HttpModule的provideZhiHuRetrofit()和provideZhihuService() 命名规则provideXXXService
     * 还有以下方法 命名规则retrofitXXXUtils  命名规则怎么开心怎么来。
     * @return
     */


    /*
    * @Provides:在modules中定义的方法是用这个注解,以此来告诉Dagger我们想要构造对象并提供这些依赖
    * @Singleton:当前提供的对象将是单例模式,一般配合Provides出现
    * */
    @Singleton
    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient.Builder provideOkHttpBuilder() {
        return new OkHttpClient.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient provideClient(OkHttpClient.Builder builder) {
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }


    protected Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String url) {
        return builder
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
