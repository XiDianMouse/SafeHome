package com.safehome.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.safehome.R;
import com.safehome.app.AppConstants;

import static com.safehome.app.AppConstants.STATE_EMPTY;
import static com.safehome.app.AppConstants.STATE_ERROR;
import static com.safehome.app.AppConstants.STATE_LOADING;
import static com.safehome.app.AppConstants.STATE_SUCCESS;
import static com.safehome.app.AppConstants.STATE_UNKNOWN;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public abstract class LoadingPage extends FrameLayout{
    private Context mContext;

    private View loadingView;//加载中界面
    private View errorView;//错误界面
    private View emptyView;//空界面
    public View contentView;//加载成功的界面

    private ImageView img;
    private AnimationDrawable mAnimationDrawable;

    public int state = AppConstants.STATE_UNKNOWN;

    public LoadingPage(Context context){
        this(context,null);
    }

    public LoadingPage(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public LoadingPage(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        mContext = context;
        init();//初始化4种界面
    }

    private void init(){
        this.setBackgroundColor(getResources().getColor(R.color.colorPageBg));
        if(loadingView==null){
            loadingView = createLoadingView();
            this.addView(loadingView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        }
        if(emptyView==null){
            emptyView = createEmptyView();
            this.addView(emptyView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        }
        if(errorView==null){
            createErrorView();
            this.addView(errorView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        }
        showPage();
    }

    private View createLoadingView(){
        loadingView = LayoutInflater.from(mContext).inflate(R.layout.basefragment_state_loading,null);
        img=(ImageView)loadingView.getRootView().findViewById(R.id.img_progress);
        //加载动画
        mAnimationDrawable = (AnimationDrawable)img.getDrawable();
        //默认进入界面就开启动画
        if(!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
        return loadingView;
    }

    private View createEmptyView(){
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.basefragment_state_empty,null);
        return emptyView;
    }

    private View createErrorView(){
        errorView = LayoutInflater.from(mContext).inflate(R.layout.basefragment_state_error,null);
        errorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_LOADING;
                showPage();
                loadData();
            }
        });
        return errorView;
    }

    public void showPage(){
        if(loadingView!=null){
            if(state==STATE_UNKNOWN || state==STATE_LOADING){
                loadingView.setVisibility(View.VISIBLE);
                //开始动画
                startAnimation();
            }else{
                //关闭动画
                stopAnimation();
                loadingView.setVisibility(View.GONE);
            }
        }
        if(state==STATE_EMPTY || state==STATE_ERROR || state==STATE_SUCCESS){
            stopAnimation();
        }
        if(emptyView!=null){
            emptyView.setVisibility(state==STATE_EMPTY ? View.VISIBLE:View.GONE);
        }

        if (errorView != null) {
            errorView.setVisibility(state == STATE_ERROR ? View.VISIBLE : View.GONE);
        }

        if (state == STATE_SUCCESS) {
            if (contentView == null) {
                contentView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
                addView(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                initView();
            }
            contentView.setVisibility(View.VISIBLE);
        } else {
            if (contentView != null) {
                contentView.setVisibility(View.GONE);
            }
        }
    }

    private void startAnimation(){
        if(!mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
    }

    private void stopAnimation(){
        if(mAnimationDrawable!=null && mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
        }
    }

    protected  abstract void initView();

    protected abstract void loadData();

    protected abstract int getLayoutId();
}
