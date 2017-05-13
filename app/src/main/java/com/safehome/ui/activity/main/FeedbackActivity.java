package com.safehome.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.blankj.utilcode.utils.LogUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.ui.activity.base.ToolbarBaseActivity;

import java.util.List;

import butterknife.OnClick;

/**
 * @auther gbh
 * Created on 2017/5/6.
 */

public class FeedbackActivity extends ToolbarBaseActivity {
    private String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=502325525&version=1";

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initUI() {
        setToolBar(toolbarBaseActivity, "意见反馈");
    }

    @OnClick({R.id.tv_qq, R.id.tv_jianshu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_qq:
                if(hasQQClientAvailable(this)){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
                }else{
                    ToastUtils.showShortToast("请先安装QQ客户端");
                }
                break;
            case R.id.tv_jianshu:
                break;
        }
    }

    public static boolean hasQQClientAvailable(Context context){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if(pinfo!=null){
            for(int i=0;i<pinfo.size();i++){
                String pn = pinfo.get(i).packageName;
                LogUtils.e("pn = "+pn);
                if(pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")){
                    return true;
                }
            }
        }
        return false;
    }
}
