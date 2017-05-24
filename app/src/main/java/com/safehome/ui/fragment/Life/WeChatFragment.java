package com.safehome.ui.fragment.Life;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.safehome.R;
import com.safehome.adapter.WeChatAdapter;
import com.safehome.app.AppConstants;
import com.safehome.bean.wechat.WXItemBean;
import com.safehome.injector.component.DaggerWeChatComponent;
import com.safehome.injector.module.fragment.WeChatModule;
import com.safehome.injector.module.http.WeChatHttpModule;
import com.safehome.listeners.OnItemClickListener;
import com.safehome.presenter.WeChatPresenter;
import com.safehome.presenter.impl.WeChatPresenterImpl;
import com.safehome.rx.RxBus;
import com.safehome.ui.fragment.BaseFragment;
import com.safehome.ui.fragment.gank.AutoLoadRecyclerView;
import com.safehome.webview.WebViewActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by quantan.liu on 2017/3/22.
 */

public class WeChatFragment extends BaseFragment<WeChatPresenterImpl>
        implements WeChatPresenter.View,SwipeRefreshLayout.OnRefreshListener,
        OnItemClickListener<WXItemBean>{

    private static final int NUM_OF_PAGE = 20;
    @BindView(R.id.rv_chat)
    AutoLoadRecyclerView rvChat;
    @BindView(R.id.srl_wechat)
    SwipeRefreshLayout srlWechat;
    Unbinder unbinder;

    private WeChatAdapter mWeChatAdapter;
    private int currentPage = 1;
    private List<WXItemBean> data;
    private CompositeSubscription searshSubscription;

    @Override
    public void refreshView(List<WXItemBean> data) {
        mWeChatAdapter.addItems(data);
    }

    @Override
    public void onClick(WXItemBean item){
        WebViewActivity.loadUrl(getActivity(),item.getUrl(),item.getTitle());
    }

    @Override
    protected void loadData() {
        mPresenter.fetchWeChatHot(NUM_OF_PAGE, currentPage);
        if (this.searshSubscription == null) {
            registerEvent();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_wechat;
    }

    @Override
    protected void initView() {
        srlWechat.setColorSchemeColors(getResources().getColor(R.color.colorTheme));
        srlWechat.setOnRefreshListener(this);
        mWeChatAdapter = (WeChatAdapter) mAdapter;
        mWeChatAdapter.setItemClickListener(this);
        rvChat.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rvChat.setAdapter(mWeChatAdapter);
    }

    @Override
    protected void initInject() {
        DaggerWeChatComponent.builder()
                .weChatHttpModule(new WeChatHttpModule())
                .weChatModule(new WeChatModule(getActivity()))
                .build().injectWeChat(this);
    }

    public void registerEvent() {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.WECHA_SEARCH, String.class)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mPresenter.fetchWXHotSearch(20, 1, s);
                    }
                });
        if (this.searshSubscription == null) {
            searshSubscription = new CompositeSubscription();
        }
        searshSubscription.add(mSubscription);
    }

    @Override
    public void onRefresh(){

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.searshSubscription != null && searshSubscription.hasSubscriptions()) {
            this.searshSubscription.unsubscribe();
        }
    }
}
