package com.safehome.injector.component;


import com.safehome.injector.module.fragment.AndroidModule;
import com.safehome.injector.module.http.GankIoHttpModule;
import com.safehome.ui.fragment.gank.AndroidFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by quantan.liu on 2017/4/8.
 */

/*
* @Component:Component从根本上来说就是一个注入器，也可以说是@Inject和@Module的桥梁，
* 它的主要作用就是连接这两个部分。将Module中产生的依赖对象自动注入到需要依赖实例的Container中。
*
*
* 命名方式推荐为:目标类名+Component,在编译后Dagger2就会生成DaggerXXXComponent这个类,
* 它是定义的 xxxComponent 的实现，在目标类中使用它就可以实现依赖注入了。
* */
@Singleton
@Component(modules = { GankIoHttpModule.class,AndroidModule.class})//指明Component查找Module的位置
public interface AndroidComponent {
    void inject(AndroidFragment androidFragment);
}
