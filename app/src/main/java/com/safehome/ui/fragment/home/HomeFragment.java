package com.safehome.ui.fragment.home;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;

import com.safehome.R;
import com.safehome.adapter.HomeFragmentPageAdapter;
import com.safehome.app.AppConstants;
import com.safehome.ui.fragment.BaseFragment;
import com.safehome.ui.fragment.home.child.PageConnectFragment;
import com.safehome.ui.fragment.home.child.PageFaceFragment;
import com.safehome.ui.fragment.home.child.PageHandFragment;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.tab_safehome)
    TabLayout tabSafehome;
    @BindView(R.id.vp_safehome)
    ViewPager vpSafehome;

    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private HomeFragmentPageAdapter mHomeFragmentPageAdapter;

    @Override
    protected void loadData() {
        setState(AppConstants.STATE_SUCCESS);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_safehome;
    }

    @Override
    protected void initView() {
        initFragmentList();
        mHomeFragmentPageAdapter = new HomeFragmentPageAdapter(getChildFragmentManager(),mFragmentList,mTitleList);
        vpSafehome.setAdapter(mHomeFragmentPageAdapter);
        tabSafehome.setTabMode(TabLayout.MODE_FIXED);
        tabSafehome.setupWithViewPager(vpSafehome);
    }

    private void initFragmentList(){
        mTitleList.add("指纹解锁");
        mTitleList.add("人脸解锁");
        mTitleList.add("连接方式");
        mFragmentList.add(new PageHandFragment());
        mFragmentList.add(new PageFaceFragment());
        mFragmentList.add(new PageConnectFragment());
    }

    @Override
    protected void initInject() {

    }
}
