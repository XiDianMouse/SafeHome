package com.safehome.injector.component;


import com.safehome.injector.module.fragment.WeChatModule;
import com.safehome.injector.module.http.WeChatHttpModule;
import com.safehome.ui.fragment.Life.WeChatFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by quantan.liu on 2017/4/8.
 */
@Singleton
@Component(modules = { WeChatHttpModule.class,WeChatModule.class})
public interface WeChatComponent {
    void injectWeChat(WeChatFragment weChatFragment);
}
