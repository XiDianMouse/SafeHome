package com.safehome.ui.fragment.home.child;

import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.ui.activity.main.HomeActivity;
import com.safehome.utils.MySelectorUtils;

import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class PageHandFragment extends BaseHomeFragment {
    @BindView(R.id.hand_showstatus)
    TextView handShowstatus;

    @BindView(R.id.hand_open)
    ImageView handOpen;

    @BindView(R.id.hand_register)
    ImageView handRegister;

    @BindView(R.id.hand_identification)
    ImageView handIdentification;

    @BindView(R.id.hand_delete)
    ImageView handDelete;

    @BindView(R.id.hand_deleteall)
    ImageView handDeleteall;

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
        } else {
        }
    }

    @OnClick({R.id.ll_hand_register, R.id.ll_hand_identification, R.id.ll_hand_open, R.id.ll_hand_delete, R.id.ll_hand_deleteall})
    public void onViewClicked(View view) {
        setAnimator(view);
        switch (mCommandStyle) {
            case 0:
                sendCmdByBluetooth(view);
                break;
            case 1:
                sendCmdByGPRS(view);
                break;
            case 2:
                sendCmdByNetwork(view);
                break;
            default:
                ToastUtils.showShortToast("请选择命令发送方式");
        }
    }


    private void sendCmdByBluetooth(View v) {
        String cmd = null;
        String data = "0000";
        switch (v.getId()) {
            case R.id.ll_hand_delete:
                cmd = "01";
                break;
            case R.id.ll_hand_deleteall:
                cmd = "02";
                break;
            case R.id.ll_hand_register:
                cmd = "03";
                break;
            case R.id.ll_hand_identification:
                cmd = "04";
                break;
            case R.id.ll_hand_open:
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
            case R.id.ll_hand_delete:
                cmd = "DeleteOne";
                break;
            case R.id.ll_hand_deleteall:
                cmd = "DeleteAll";
                break;
            case R.id.ll_hand_register:
                cmd = "EnrollFinger";
                break;
            case R.id.ll_hand_identification:
                cmd = "IdentifyFinger";
                break;
            case R.id.ll_hand_open:
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

    private void sendCmdByNetwork(View view) {
        String cmd = null;
        switch (view.getId()) {
            case R.id.ll_hand_delete:
                //cmd = "DeleteOne";
                cmd = "15702923681+108.925329,34.238966";
                break;
            case R.id.ll_hand_deleteall:
                cmd = "DeleteAll";
                break;
            case R.id.ll_hand_register:
                cmd = "EnrollFinger";
                break;
            case R.id.ll_hand_identification:
                cmd = "IdentifyFinger";
                break;
            case R.id.ll_hand_open:
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
        submitTask(cmd,null);
    }

    class CustomInterpolator implements TimeInterpolator{
        @Override
        public float getInterpolation(float input){
            input *= 0.8f;
            return input*input;
        }
    }

    /**
     * 使用ValueAnimator实现图片缩放动画
     */
    public void setAnimator(View view){
        ImageView imageView = null;
        switch(view.getId()){
            case R.id.ll_hand_delete:
                imageView = handDelete;
                break;
            case R.id.ll_hand_deleteall:
                imageView = handDeleteall;
                break;
            case R.id.ll_hand_register:
                imageView = handRegister;
                break;
            case R.id.ll_hand_identification:
                imageView = handIdentification;
                break;
            case R.id.ll_hand_open:
                imageView = handOpen;
                break;
        }
        scaleValueAnimator(imageView);
    }

    //使用ValueAnimator实现图片缩放
    private void scaleValueAnimator(final ImageView imageView){
        //1.设置目标属性名及属性变化的初始值和结束值
        PropertyValuesHolder propertyValuesHolderScaleX = PropertyValuesHolder.ofFloat("scaleX",1.0f,0.0f);
        PropertyValuesHolder propertyValuesHolderScaleY = PropertyValuesHolder.ofFloat("scaleY",1.0f,0.0f);
        ValueAnimator mAnimator = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolderScaleX,propertyValuesHolderScaleY);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //3.根据属性名变化的值分别为ImageView目标
                float animatorValueScaleX = (float)animation.getAnimatedValue("scaleX");
                float animatorValueScaleY = (float)animation.getAnimatedValue("scaleY");
                imageView.setScaleX(animatorValueScaleX);
                imageView.setScaleY(animatorValueScaleY);
            }
        });
        //4.为ValueAnimator设置自定义的Interpolator
        mAnimator.setInterpolator(new CustomInterpolator());
        //5.设置动画持续时间、是否重复以及重复次数等属性
        mAnimator.setDuration(200);
        mAnimator.setRepeatCount(1);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        //6.位ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(imageView);
        mAnimator.start();
    }

    public void setShowAreaBT(String text){
        if(text.startsWith("EB 90 00 ")){
            int start = 0;
            for(int i=0;i<3;i++){
                start = start+3;
            }
            text = text.substring(start,start+2);
            if(text.startsWith("8")){
                int cmd = Integer.parseInt(text.substring(1),16);
                FutureTask<Integer> futureTask = mBluetoothMap.get("0"+cmd);
                if(futureTask!=null && !futureTask.isDone()){
                    futureTask.cancel(true);
                }
                else{//没进去
                }
                updateArea(cmd);
            }
        }
        else if(text.startsWith("EB 90 02 ")){
            int start = 0;
            for(int i=0;i<3;i++){
                start = start+3;
            }
            String cmd = text.substring(start,start+2);
            String hexHigh = text.substring(start+3,start+5);
            String hexLow = text.substring(start+6,start+8);
            int num = Integer.parseInt(hexHigh+hexLow,16);
            if(cmd.startsWith("8")){
                updateArea(cmd,num);
            }
        }
    }

    protected void updateArea(int cmd) {
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

    public void updateArea(String msg) {
        handShowstatus.setText(msg);
    }

    public void updateArea(String cmd, int num) {
        switch (Integer.parseInt(cmd.substring(1), 16)) {
            case 1:
                handShowstatus.setText("删除单个指纹成功后,当前指纹个数为:" + num);
                break;
            case 3:
                handShowstatus.setText("注册指纹指令执行后,当前指纹个数为:" + num);
                break;
        }
    }

    public void setShowAreaNetwork(String data){
        updateArea("服务器返回数据为:"+data);
        String cmd = data.substring(2);
        FutureTask<Integer> futureTask = mNetWorkMap.get(cmd);
        if(futureTask!=null && !futureTask.isDone()){
            futureTask.cancel(true);
        }
    }
}
