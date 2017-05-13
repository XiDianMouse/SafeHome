package com.safehome.ui.fragment.gank;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.adapter.GankIoAndroidAdapter;
import com.safehome.bean.gankio.GankIoDataBean;
import com.safehome.bean.gankio.GankIoDataBean.ResultBean;
import com.safehome.injector.component.fragment.DaggerAndroidComponent;
import com.safehome.injector.module.fragment.AndroidModule;
import com.safehome.injector.module.http.GankIoHttpModule;
import com.safehome.listeners.OnItemClickListener;
import com.safehome.presenter.GankIoAndroidPresenter.View;
import com.safehome.presenter.impl.GankIoAndroidPresenterImpl;
import com.safehome.ui.fragment.BaseFragment;

import java.util.List;

import butterknife.BindView;


/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class AndroidFragment extends BaseFragment<GankIoAndroidPresenterImpl>
        implements View, OnRefreshListener, OnItemClickListener<ResultBean>{

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
        srlAndroid.setColorSchemeColors(getResources().getColor(R.color.colorTheme));
        srlAndroid.setOnRefreshListener(this);
        mGankIoAndroidAdapter = (GankIoAndroidAdapter)mAdapter;
        mGankIoAndroidAdapter.setItemClickListener(this);
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
            srlAndroid.setRefreshing(false);
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
        ToastUtils.showShortToast(item.getUrl());
    }
}
