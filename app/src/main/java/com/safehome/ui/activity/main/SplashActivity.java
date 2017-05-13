package com.safehome.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.safehome.R;
import com.safehome.ui.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @auther gbh
 * Created on 2017/5/5.
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_pic)
    ImageView ivPic;
    private Unbinder bind;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreate(Bundle savedInstanced) {
        super.onCreate(savedInstanced);
        bind = ButterKnife.bind(this);
        ivPic.setImageResource(R.mipmap.haden);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 200);
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(bind!=null){
            bind.unbind();
        }
    }
}
