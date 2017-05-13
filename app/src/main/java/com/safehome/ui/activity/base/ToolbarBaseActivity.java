package com.safehome.ui.activity.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.safehome.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @auther gbh
 * Created on 2017/5/6.
 */

public abstract class ToolbarBaseActivity extends BaseActivity {
    protected Toolbar toolbarBaseActivity;

    FrameLayout flToolbarBase;

    private Unbinder mBind;
    private View mContentView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_toolbar_base;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbarBaseActivity = (Toolbar) findViewById(R.id.toolbar_base_activity);
        flToolbarBase = (FrameLayout) findViewById(R.id.fl_toolbar_base);
        mContentView = LayoutInflater.from(this).inflate(getContentLayoutId(),null);
        flToolbarBase.addView(mContentView);
        mBind = ButterKnife.bind(this,mContentView);
        setToolBar(toolbarBaseActivity,"");
        initUI();
    }

    protected abstract void initUI();

    public abstract int getContentLayoutId();

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mBind!=null){
            mBind.unbind();
        }
    }
}
