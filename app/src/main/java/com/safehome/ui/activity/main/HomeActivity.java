package com.safehome.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.blankj.utilcode.utils.SPUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.adapter.HomeFragmentPageAdapter;
import com.safehome.adapter.MenuAdapter;
import com.safehome.bean.menu.MenuItem;
import com.safehome.bluetooth.BTController;
import com.safehome.gprs.PhoneControl;
import com.safehome.listeners.OnItemClickListener;
import com.safehome.protocol.BTProtocol;
import com.safehome.ui.activity.base.BaseActivity;
import com.safehome.ui.fragment.gank.AndroidFragment;
import com.safehome.ui.fragment.home.HomeFragment;
import com.safehome.ui.fragment.home.child.PageConnectFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @auther gbh
 * Created on 2017/5/5.
 */

public class HomeActivity extends BaseActivity implements OnItemClickListener<MenuItem>{
    public static final int MSG_UPDATE_BT_STATE = 0x00;
    public static final int MSG_RECEIVE_DATA = 0x01;
    public static final int MSG_SEND_HANDCMD_FAIL = 0x02;
    public static final int MSG_HANDCMD_UNRESPONSE = 0x03;
    public static int commandStyle;//0:蓝牙　1:GPRS
    private boolean isBTConnected;
    public BTController btController;
    public static MsgHandler msgHandler;
    private WheelchairListener mListener;
    private PhoneControl mPhoneControl;
    private PageConnectFragment mPageConnectFragment;


    @BindView(R.id.fl_title_menu)
    FrameLayout nvMenu;

    @BindView(R.id.rg_home_viewpager_control)
    RadioGroup rgHomeViewpagerControl;

    @BindView(R.id.toolbar)
    Toolbar tbToolbar;

    @BindView(R.id.vp_content)
    ViewPager vpContent;

    @BindView(R.id.dl_layout)
    DrawerLayout dlLayout;

    @BindView(R.id.menu_recyclerview)
    RecyclerView menuRecyclerview;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setSupportActionBar(tbToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(false);//不显示返回键
        supportActionBar.setDisplayShowTitleEnabled(false);//去除默认标题
        btController = BTController.getInstance(this);
        isBTConnected = false;
        commandStyle = -1;
        msgHandler = new MsgHandler(this);
        mPhoneControl = PhoneControl.getInstance(this);
        initFragmentView();
        initMenuView();
        SPUtils spUtils = new SPUtils("home_list");
        if (!spUtils.getBoolean("home_list_boolean")) {
            spUtils.putString("home_list", "知乎日报&&知乎热门&&知乎主题&&知乎专栏&&");
            spUtils.putBoolean("home_list_boolean", true);
        }
    }

    private void initFragmentView() {
        rgHomeViewpagerControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home_pager:
                        vpContent.setCurrentItem(0);
                        break;
                    case R.id.rb_music_pager:
                        vpContent.setCurrentItem(1);
                        break;
                    case R.id.rb_friend_pager:
                        vpContent.setCurrentItem(2);
                        break;
                }
            }
        });

        List<Fragment> mFragmentList = new ArrayList<>();
        mPageConnectFragment = new PageConnectFragment();
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new AndroidFragment());
        mFragmentList.add(mPageConnectFragment);
        vpContent.setAdapter(new HomeFragmentPageAdapter(getSupportFragmentManager(), mFragmentList));
        vpContent.setCurrentItem(0);
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rgHomeViewpagerControl.check(R.id.rb_home_pager);
                        break;
                    case 1:
                        rgHomeViewpagerControl.check(R.id.rb_music_pager);
                        break;
                    case 2:
                        rgHomeViewpagerControl.check(R.id.rb_friend_pager);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initMenuView() {
        menuRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MenuAdapter menuAdapter = new MenuAdapter();
        menuAdapter.addItems(prepareMenuItems());
        menuAdapter.setItemClickListener(this);
        menuRecyclerview.setAdapter(menuAdapter);
    }

    private List<MenuItem> prepareMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.drawable.theme_color,"个性换肤"));
        menuItems.add(new MenuItem(R.drawable.about_us,"关于我们"));
        menuItems.add(new MenuItem(R.drawable.setting,"设置"));
        menuItems.add(new MenuItem(R.drawable.feedback,"意见反馈"));
        menuItems.add(new MenuItem(R.drawable.exit_app,"退出"));
        return menuItems;
    }

    @Override
    public void onBackPressed() {
        if (dlLayout.isDrawerOpen(GravityCompat.START)) {
            dlLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dlLayout.isDrawerOpen(GravityCompat.START)) {
                dlLayout.closeDrawer(GravityCompat.START);
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @OnClick({R.id.fl_title_menu, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_title_menu:
                dlLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.login:
                ToastUtils.showShortToast("尚未开发");
                break;
        }
    }

    @Override
    public void onClick(MenuItem item){
        switch(item.iconResId){
            case R.drawable.theme_color:
                ToastUtils.showShortToast("尚未开发");
                break;
            case R.drawable.about_us:
                startActivity(new Intent(this,AboutUsActivity.class));
                break;
            case R.drawable.setting:
                ToastUtils.showShortToast("尚未开发");
                break;
            case R.drawable.feedback:
                startActivity(new Intent(this,FeedbackActivity.class));
                break;
            case R.drawable.exit_app:
                killAll();
                break;
        }
    }

    public interface WheelchairListener {
        void onReceiveData(byte[] data);
    }


    public void updateBTState(boolean isConnected) {
        if (isConnected) {
            ToastUtils.showShortToast("已连接");
            isBTConnected = true;
        } else {
            ToastUtils.showShortToast("已断开");
            isBTConnected = false;
        }
    }

    public boolean sendData(byte[] data) {
        if (data != null) {
            if (isBTConnected) {
                btController.sendData(data);
                return true;
            }
        }
        return false;
    }

    public void sendDataByGPRS(String data) {
        mPhoneControl.sendMessage("17691078058", data);
    }

    public void onReceiveData(String address, byte[] data) {
        String receivedData = BTProtocol.toHex(data);
        //mPageHandFragment.setShowArea(receivedData);
        if (mListener != null) {
            mListener.onReceiveData(data);
        }
    }

    public void updateHandCmdStatus(String msg) {
        //mPageHandFragment.updateCmdStatus(msg);
    }

    //创建静态内部类，防止内存泄漏
    public static class MsgHandler extends Handler {
        private WeakReference<HomeActivity> mRef;

        public MsgHandler(HomeActivity activity) {
            this.mRef = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {//此方法在ui线程运行
            HomeActivity activity = this.mRef.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_BT_STATE:
                    activity.updateBTState(msg.arg1 != 0);
                    break;
                case MSG_RECEIVE_DATA:
                    Object[] data = (Object[]) msg.obj;
                    activity.onReceiveData((String) data[0], (byte[]) data[1]);
                    break;
                case MSG_SEND_HANDCMD_FAIL:
                    ToastUtils.showShortToast("数据发送失败,请确认是否连接设备!");
                    break;
                case MSG_HANDCMD_UNRESPONSE:
//                    switch (activity.mPageHandFragment.getCurrentFragment()) {
//                        case 0:
//                            ToastUtils.showShortToast("未收到设备反馈,请检测并重新发送");
//                            break;
//                    }
                    break;
            }
        }
    }
}
