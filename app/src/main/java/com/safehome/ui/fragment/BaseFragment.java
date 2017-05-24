package com.safehome.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safehome.http.LifeSubscription;
import com.safehome.http.Stateful;
import com.safehome.presenter.BasePresenter;
import com.safehome.ui.activity.main.HomeActivity;
import com.safehome.view.LoadingPage;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment
        implements LifeSubscription,Stateful {
    /*
    * @Inject:通常在需要依赖的地方使用这个注解。换句话说，你用它告诉Dagger这个类或者字段需要依赖注入。
    * 这样，Dagger就会构造一个这个类的实例并满足他们的依赖。
    * */
    @Inject
    protected P mPresenter;

    @Inject
    protected RecyclerView.Adapter mAdapter;

    protected HomeActivity activity;

    public LoadingPage mLoadingPage;

    private boolean mIsVisible = false;//fragment是否显示了
    private boolean isPrepared = false;
    private boolean isFirst = true;//只加载一次界面

    protected View contentView;
    private Unbinder bind;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (HomeActivity)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanced){
        if(mLoadingPage==null){
            mLoadingPage = new LoadingPage(getContext()) {
                @Override
                protected void initView() {
                    if(isFirst){
                        BaseFragment.this.contentView = this.contentView;
                        bind = ButterKnife.bind(BaseFragment.this,contentView);
                        BaseFragment.this.initView();
                        isFirst = false;
                    }
                }

                @Override
                protected void loadData() {
                    BaseFragment.this.loadData();
                }

                @Override
                protected int getLayoutId() {
                    return BaseFragment.this.getLayoutId();
                }
            };
        }
        isPrepared = true;
        loadBaseData();
        return mLoadingPage;
    }

    //在这里实现Fragment数据的缓加载
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){//fragment可见
            mIsVisible = true;
            onVisible();
        }else{
            mIsVisible = false;
            onInvisible();
        }
    }

    @Override
    public void setState(int state){
        mLoadingPage.state = state;
        mLoadingPage.showPage();
    }

    protected void onInvisible(){

    }

    //显示时加载数据
    protected void onVisible(){
        if(isFirst){
            initInject();
            if(mPresenter!=null){
                mPresenter.attachView(this);
            }
        }
        loadBaseData();//根据获取的数据来调用showView切换界面
    }

    public void loadBaseData(){
        if(!mIsVisible || !isPrepared || !isFirst){
            return;
        }
        loadData();
    }

    protected abstract void loadData();

    protected abstract int getLayoutId();

    protected abstract void initView();

    //dagger2注入
    protected abstract void initInject();

    private CompositeSubscription mCompositeSubscription;

    @Override
    public void bindSubscription(Subscription subscription){
        if(this.mCompositeSubscription==null){
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        if(bind!=null){
            bind.unbind();
        }
        if(this.mCompositeSubscription!=null & mCompositeSubscription.hasSubscriptions()){
            this.mCompositeSubscription.unsubscribe();
        }
        if(mPresenter!=null){
            mPresenter.detachView();
        }
    }
}
