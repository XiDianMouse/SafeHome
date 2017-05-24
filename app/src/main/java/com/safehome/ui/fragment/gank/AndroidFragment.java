package com.safehome.ui.fragment.gank;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;

import com.safehome.R;
import com.safehome.adapter.GankIoAndroidAdapter;
import com.safehome.bean.gankio.GankIoDataBean;
import com.safehome.bean.gankio.GankIoDataBean.ResultBean;
import com.safehome.injector.component.DaggerAndroidComponent;
import com.safehome.injector.module.fragment.AndroidModule;
import com.safehome.injector.module.http.GankIoHttpModule;
import com.safehome.listeners.OnItemClickListener;
import com.safehome.presenter.GankIoAndroidPresenter.View;
import com.safehome.presenter.impl.GankIoAndroidPresenterImpl;
import com.safehome.ui.fragment.BaseFragment;
import com.safehome.webview.WebViewActivity;

import java.util.List;

import butterknife.BindView;


/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class AndroidFragment extends BaseFragment<GankIoAndroidPresenterImpl>
        implements View, OnRefreshListener,
            OnItemClickListener<ResultBean>,AutoLoadRecyclerView.OnLoadListener{

    @BindView(R.id.rv_android)
    AutoLoadRecyclerView rvAndroid;
    @BindView(R.id.srl_android)
    SwipeRefreshLayout srlAndroid;

    private GankIoAndroidAdapter mGankIoAndroidAdapter;
    private int page;
    private final static int PRE_PAGE = 10;
    private boolean isRefresh = false;

    @Override
    public void initInject() {
        DaggerAndroidComponent.builder()
                .gankIoHttpModule(new GankIoHttpModule())
                .androidModule(new AndroidModule(getActivity()))
                .build().inject(this);
    }

    @Override
    public void initView() {
        srlAndroid.setColorSchemeColors(getResources().getColor(R.color.colorTheme));//设置进度动画的颜色
        srlAndroid.setOnRefreshListener(this);//设置手势滑动监听器
        mGankIoAndroidAdapter = (GankIoAndroidAdapter)mAdapter;
        mGankIoAndroidAdapter.setItemClickListener(this);
        rvAndroid.setOnLoadListener(this);
        rvAndroid.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        rvAndroid.setAdapter(mGankIoAndroidAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fragment_android;
    }

    @Override
    public void loadData() {
        mPresenter.fetchGankIoData(page, PRE_PAGE);
    }

    @Override
    public void onRefresh() {
        page = 0;
        isRefresh = true;
        mPresenter.fetchGankIoData(page, PRE_PAGE);
    }

    @Override
    public void refreshView(List<ResultBean> data) {
        if (isRefresh) {
            srlAndroid.setRefreshing(false);//设置组件的刷新状态,false:关闭刷新,否则刷新球一直在转
            isRefresh = false;
            mGankIoAndroidAdapter.addItems(data);
        } else {
            srlAndroid.setEnabled(true);
            page++;
            mGankIoAndroidAdapter.addItems(data);
        }
    }

    @Override
    public void onClick(GankIoDataBean.ResultBean item) {
        WebViewActivity.loadUrl(getActivity(),item.getUrl(),item.getSource());
    }

    @Override
    public void onLoad(){
        loadData();
        srlAndroid.setEnabled(false);//控制是否可以刷新
    }
}
