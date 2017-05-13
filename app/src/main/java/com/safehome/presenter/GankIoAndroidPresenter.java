package com.safehome.presenter;

import com.safehome.bean.gankio.GankIoDataBean;

import java.util.List;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public interface GankIoAndroidPresenter {

    interface View extends BaseView<List<GankIoDataBean.ResultBean>> {
    }

    interface Presenter{
        void fetchGankIoData(int page, int pre_page);
    }
}
