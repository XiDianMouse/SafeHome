package com.safehome.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class HomeFragmentPageAdapter  extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> mTitleList;

    public HomeFragmentPageAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
        this(fragmentManager,fragmentList,null);
    }

    public HomeFragmentPageAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> mTitleList){
        super(fm);
        this.fragmentList = fragmentList;
        this.mTitleList = mTitleList;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
    /**
     * 首页显示title，每日推荐等..
     * 若有问题，移到对应单独页面
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleList != null) {
            return mTitleList.get(position);
        } else {
            return "";
        }
    }
}
