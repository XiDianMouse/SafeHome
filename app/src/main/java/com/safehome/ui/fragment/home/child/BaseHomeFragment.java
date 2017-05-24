package com.safehome.ui.fragment.home.child;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.bluetooth.BTController;
import com.safehome.communication.SendTask;
import com.safehome.dialog.BluetoothDialog;
import com.safehome.gprs.PhoneControl;
import com.safehome.protocol.BTProtocol;
import com.safehome.tcp.TcpController;
import com.safehome.ui.activity.main.HomeActivity;
import com.safehome.utils.StringUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public abstract class BaseHomeFragment extends Fragment{
    public static TcpController mTcpController;
    public static int mCommandStyle;//0:蓝牙 1:GPRS 2:公网
    public static boolean mIsConnected;
    public static ConcurrentHashMap<String,FutureTask<Integer>> mBluetoothMap;
    public static ConcurrentHashMap<String,FutureTask<Integer>> mNetWorkMap;

    protected HomeActivity activity;
    private boolean hasCreateView;
    private boolean isFragmentVisible;
    protected View rootView;
    protected BluetoothDialog mBluetoothDialog;
    protected BTController mBTController;
    private ThreadPoolExecutor mThreadPoolExector;
    private PhoneControl mPhoneControl;
    private Unbinder bind;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (HomeActivity)activity;
        mBTController = BTController.getInstance();
        mBTController.open();
        mPhoneControl = PhoneControl.getInstance(this.activity);
        afterOnAttach(this.activity);
    }

    protected void afterOnAttach(HomeActivity activity){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initVariable();
    }
    private void initVariable(){
        hasCreateView = false;
        isFragmentVisible = false;
        mBluetoothMap = new ConcurrentHashMap<>();
        mNetWorkMap = new ConcurrentHashMap<>();
        mThreadPoolExector = new ThreadPoolExecutor(1,
                                                    1,
                                                    1,
                                                    TimeUnit.SECONDS,
                                                    new ArrayBlockingQueue<Runnable>(5),
                                                    new CustomThreadFactory(),
                                                    new CustomRejectedExecutionHandler());
        mCommandStyle = -1;
    }

    private class CustomThreadFactory implements ThreadFactory{
        private AtomicInteger count = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r){
            Thread t = new Thread(r);
            t.setName("Thread-"+count.getAndIncrement());
            return t;
        }
    }

    private class CustomRejectedExecutionHandler implements RejectedExecutionHandler{
        @Override
        public void rejectedExecution(Runnable r,ThreadPoolExecutor executor){
            ToastUtils.showShortToast("任务添加过于频繁");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(rootView==null) {
            rootView = inflater.inflate(getContentViewResId(), container, false);
            bind = ButterKnife.bind(this,rootView);
            initWidgets();
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVieibleToUser){//用于告诉系统该Fragment的UI是否可见
        super.setUserVisibleHint(isVieibleToUser);
        if(rootView==null){
            return;
        }
        hasCreateView = true;
        if(isVieibleToUser){
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if(isFragmentVisible){
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    protected abstract int getContentViewResId();

    protected abstract void initWidgets();

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        if(!hasCreateView && getUserVisibleHint()){//getUserVisibleHint：获得Fragment的可见状态
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
        }
    }

    protected void submitTask(String cmd,String data){//使用线程池提交任务
        FutureTask<Integer> preTask = null;
        switch(mCommandStyle){
            case 0://蓝牙
                preTask = mBluetoothMap.get(cmd);
                break;
            case 2://公网
                preTask = mNetWorkMap.get(cmd);
                break;
        }

        if(preTask!=null && !preTask.isDone()){//之前的任务尚未完成,直接取消,不添加任务,先将前面未执行的相同任务完成
            return;
        }
        //前一个任务不存在,或者已经完成
        String str = null;
        switch(mCommandStyle){
            case 0:
                str = BTProtocol.getStr(cmd,data);
                break;
            case 2:
                str = cmd;
                break;
        }

        SendTask task = new SendTask(this,str);
        FutureTask<Integer> futureTask = new FutureTask<>(task);
        switch (mCommandStyle){
            case 0:
                mBluetoothMap.put(cmd,futureTask);//保存任务
               break;
            case 2:
                mNetWorkMap.put(cmd,futureTask);
                break;
        }
        mThreadPoolExector.submit(futureTask);
    }

    public boolean sendData(String cmd) {
        if (cmd != null) {
            switch(mCommandStyle){
                case 0:
                    if (mIsConnected) {
                        mBTController.sendData(StringUtils.hexStringToByte(cmd));
                    }
                    break;
                case 2:
                    sendNetworkData(cmd);
                    break;
            }
            return true;
        }
        return false;
    }

    private void sendNetworkData(String cmd){
        Handler handler = mTcpController.mThreadHandler;
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("cmd",cmd);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    public void sendDataByGPRS(String data) {
        mPhoneControl.sendMessage("17691078058", data);
    }

    protected void onFragmentVisibleChange(boolean isVisible){
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(bind!=null){
            bind.unbind();
        }
    }
}
