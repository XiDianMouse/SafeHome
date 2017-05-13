package com.safehome.ui.fragment.home.child;

import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.ui.activity.main.HomeActivity;
import com.safehome.utils.MySelectorUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class PageHandFragment extends BaseHomeFragment {
    @BindView(R.id.hand_open)
    ImageButton handOpen;
    @BindView(R.id.hand_showstatus)
    TextView handShowstatus;

    private StateListDrawable openStateListDrawable;
    private StateListDrawable closeStateListDrawable;
    private boolean openFlag;

    public PageHandFragment() {
        openFlag = false;
    }

    @Override
    protected void afterOnAttach(HomeActivity activity) {
        openStateListDrawable = MySelectorUtils.addStateDrawable(activity,
                R.drawable.hand_open,
                R.drawable.hand_openorclose_press,
                R.drawable.hand_openorclose_press);
        closeStateListDrawable = MySelectorUtils.addStateDrawable(activity,
                R.drawable.hand_close,
                R.drawable.hand_openorclose_press,
                R.drawable.hand_openorclose_press);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.layout_fragment_hand;
    }

    @Override
    protected void initWidgets() {
        handOpen.setBackgroundDrawable(openStateListDrawable);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {//do things when fragment is visible
            CURRENT_FRAGMENT = 0;
        } else {
        }
    }

    @OnClick({R.id.hand_open, R.id.hand_delete, R.id.hand_register, R.id.hand_identification, R.id.hand_deleteall})
    public void onViewClicked(View view) {
        switch(HomeActivity.commandStyle) {
			case 0:
				sendCmdByBluetooth(view);
				break;
			case 1:
				sendCmdByGPRS(view);
				break;
			default:
                ToastUtils.showShortToast("请选择命令发送方式");
		}
    }

    private void sendCmdByBluetooth(View v) {
        String cmd = null;
        String data = "0000";
        switch (v.getId()) {
            case R.id.hand_delete:
                cmd = "01";
                break;
            case R.id.hand_deleteall:
                cmd = "02";
                break;
            case R.id.hand_register:
                cmd = "03";
                break;
            case R.id.hand_identification:
                cmd = "04";
                break;
            case R.id.hand_open:
                if (!openFlag) {//尚未开启
                    cmd = "05";
                } else {
                    cmd = "06";
                }
                break;
        }
        submitTask(cmd, data);
    }

    private void sendCmdByGPRS(View v) {
        String cmd = null;
        switch (v.getId()) {
            case R.id.hand_delete:
                cmd = "DeleteOne";
                break;
            case R.id.hand_deleteall:
                cmd = "DeleteAll";
                break;
            case R.id.hand_register:
                cmd = "EnrollFinger";
                break;
            case R.id.hand_identification:
                cmd = "IdentifyFinger";
                break;
            case R.id.hand_open:
                if (!openFlag) {//尚未开启
                    openFlag = true;
                    cmd = "OpenFinger";
                    handOpen.setBackgroundDrawable(closeStateListDrawable);
                } else {
                    openFlag = false;
                    cmd = "CloseFinger";
                    handOpen.setBackgroundDrawable(openStateListDrawable);
                }
                break;
        }
        sendDataByGPRS(cmd);
    }

    public void updateCmdStatus(String msg) {
        handShowstatus.setText(msg);
    }

    @Override
    protected void updateHandAreaSucess(int cmd) {
        switch (cmd) {
            case 1:
                handShowstatus.setText("指令:" + "删除单个指纹·····" + "发送成功");
                break;
            case 2:
                handShowstatus.setText("指令:" + "删除全部指纹·····" + "发送成功");
                break;
            case 3:
                handShowstatus.setText("指令:" + "注册指纹·····" + "发送成功");
                break;
            case 4:
                handShowstatus.setText("指令:" + "识别指纹·····" + "发送成功");
                break;
            case 5:
                handShowstatus.setText("指令:" + "打开指纹·····" + "发送成功");
                openFlag = true;
                handOpen.setBackgroundDrawable(closeStateListDrawable);
                break;
            case 6:
                handShowstatus.setText("指令:" + "关闭指纹·····" + "发送成功");
                openFlag = false;
                handOpen.setBackgroundDrawable(openStateListDrawable);
                break;
        }
    }

    @Override
    protected void updateHandAreaSucessState(String cmd, int num) {
        switch (Integer.parseInt(cmd.substring(1), 16)) {
            case 1:
                handShowstatus.setText("删除单个指纹成功后,当前指纹个数为:" + num);
                break;
            case 3:
                handShowstatus.setText("注册指纹指令执行后,当前指纹个数为:" + num);
                break;
        }
    }
}
