package com.safehome.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
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
import com.safehome.ItemEntry.MenuEntry;
import com.safehome.R;
import com.safehome.adapter.HomeFragmentPageAdapter;
import com.safehome.adapter.MenuAdapter;
import com.safehome.listeners.OnItemClickListener;
import com.safehome.ui.activity.base.BaseActivity;
import com.safehome.ui.fragment.Life.WeChatFragment;
import com.safehome.ui.fragment.gank.AndroidFragment;
import com.safehome.ui.fragment.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @auther gbh
 * Created on 2017/5/5.
 */

public class HomeActivity extends BaseActivity implements OnItemClickListener<MenuEntry>{
    private HomeFragment mHomeFragment;


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
        mHomeFragment = new HomeFragment();
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
        mFragmentList.add(mHomeFragment);
        mFragmentList.add(new AndroidFragment());
        mFragmentList.add(new WeChatFragment());
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
        MenuAdapter menuAdapter = new MenuAdapter(this);
        menuAdapter.addItems(prepareMenuItems());
        menuAdapter.setItemClickListener(this);
        menuRecyclerview.setAdapter(menuAdapter);
    }

    private List<MenuEntry> prepareMenuItems() {
        List<MenuEntry> menuItems = new ArrayList<>();
        menuItems.add(new MenuEntry(R.drawable.theme_color,"个性换肤"));
        menuItems.add(new MenuEntry(R.drawable.about_us,"关于我们"));
        menuItems.add(new MenuEntry(R.drawable.setting,"设置"));
        menuItems.add(new MenuEntry(R.drawable.feedback,"意见反馈"));
        menuItems.add(new MenuEntry(R.drawable.exit_app,"退出"));
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
    public void onClick(MenuEntry item){
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
}
