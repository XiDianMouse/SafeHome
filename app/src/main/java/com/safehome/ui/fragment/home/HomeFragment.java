package com.safehome.ui.fragment.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.safehome.R;
import com.safehome.adapter.HomeFragmentPageAdapter;
import com.safehome.app.AppConstants;
import com.safehome.bluetooth.BTController;
import com.safehome.listeners.OnReceiverListener;
import com.safehome.tcp.TcpController;
import com.safehome.ui.fragment.BaseFragment;
import com.safehome.ui.fragment.home.child.PageConnectFragment;
import com.safehome.ui.fragment.home.child.PageFaceFragment;
import com.safehome.ui.fragment.home.child.PageHandFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class HomeFragment extends BaseFragment implements OnReceiverListener<String>{
    public static final int MSG_SEND_FAIL = 0x00;
    public static final int MSG_UNRESPONSE = 0x01;
    public static final int MSG_NETWORKRESPONSE = 0x02;

    @BindView(R.id.tab_safehome)
    TabLayout tabSafehome;
    @BindView(R.id.vp_safehome)
    ViewPager vpSafehome;

    public static MsgHandler mMsgHandler;
    protected BTController mBTController;
    protected TcpController mTcpController;

    private ArrayList<String> mTitleList = new ArrayList<>();
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private HomeFragmentPageAdapter mHomeFragmentPageAdapter;
    private PageHandFragment mPageHandFragment;
    private PageFaceFragment mPageFaceFragment;
    private PageConnectFragment mPageConnectFragment;

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
        mMsgHandler = new MsgHandler(this);
        mBTController = BTController.getInstance(activity,this);
        mTcpController = TcpController.getInstance(this);
        mPageHandFragment = new PageHandFragment();
        mPageFaceFragment = new PageFaceFragment();
        mPageConnectFragment = new PageConnectFragment();
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
        mFragmentList.add(mPageHandFragment);
        mFragmentList.add(mPageFaceFragment);
        mFragmentList.add(mPageConnectFragment);
    }

    @Override
    protected void initInject() {

    }

    @Override
    public void onReceive(String data,String description){
        if(description!=null){//蓝牙
            switch(vpSafehome.getCurrentItem()){
                case 0:
                    mPageHandFragment.setShowAreaBT(data);
                    break;
                case 1:
                    break;
            }
        } else{//网络(运行在子线程中)
            Message message = mMsgHandler.obtainMessage(MSG_NETWORKRESPONSE);
            Bundle bundle = new Bundle();
            bundle.putString("receive",data);
            message.setData(bundle);
            message.sendToTarget();
        }
    }

    //创建静态内部类，防止内存泄漏
    public static class MsgHandler extends Handler {
        private WeakReference<HomeFragment> mRef;

        public MsgHandler(HomeFragment fragment) {
            this.mRef = new WeakReference<>(fragment);
        }

        public void handleMessage(Message msg) {//此方法在ui线程运行
            HomeFragment fragment = this.mRef.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SEND_FAIL:
                    switch (fragment.vpSafehome.getCurrentItem()){
                        case 0:
                            fragment.mPageHandFragment.updateArea("数据发送失败,请确认是否连接设备!");
                            break;
                    }
                    break;
                case MSG_UNRESPONSE:
                    switch (fragment.vpSafehome.getCurrentItem()){
                        case 0:
                            fragment.mPageHandFragment.updateArea("未收到设备反馈,请检测并重新发送");
                            break;
                    }
                    break;
                case MSG_NETWORKRESPONSE:
                    switch (fragment.vpSafehome.getCurrentItem()){
                        case 0:
                            Bundle bundle = msg.getData();
                            String data = bundle.getString("receive");
                            fragment.mPageHandFragment.setShowAreaNetwork(data);
                            break;
                    }
            }
        }
    }
}
