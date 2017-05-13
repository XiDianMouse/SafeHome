package com.safehome.ui.activity.main;

import com.safehome.R;
import com.safehome.ui.activity.base.ToolbarBaseActivity;

import butterknife.OnClick;

/**
 * @auther gbh
 * Created on 2017/5/6.
 */

public class AboutUsActivity extends ToolbarBaseActivity {

    @Override
    protected void initUI() {
        setToolBar(toolbarBaseActivity, "关于我们");
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_about_us;
    }

    @OnClick(R.id.cv_github)
    public void onViewClicked() {
    }
}
