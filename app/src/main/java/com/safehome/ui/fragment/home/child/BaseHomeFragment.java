package com.safehome.ui.fragment.home.child;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safehome.communication.SendTask;
import com.safehome.protocol.BTProtocol;
import com.safehome.ui.activity.main.HomeActivity;
import com.safehome.utils.MyLog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public abstract class BaseHomeFragment extends Fragment {
    protected int CURRENT_FRAGMENT;
    private HomeActivity activity;
    private boolean hasCreateView;
    private boolean isFragmentVisible;
    protected View rootView;
    private ExecutorService executorService;
    private ConcurrentHashMap<String,FutureTask<Integer>> taskMap;
    private Unbinder bind;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (HomeActivity)activity;
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
        CURRENT_FRAGMENT = 0;//初始界面为指纹识别界面
        hasCreateView = false;
        isFragmentVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(rootView==null) {
            rootView = inflater.inflate(getContentViewResId(), container, false);
            bind = ButterKnife.bind(this,rootView);
            initWidgets();
            initSendCmd();
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

    private void initSendCmd(){
        taskMap = new ConcurrentHashMap<String,FutureTask<Integer>>();
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        if(!hasCreateView && getUserVisibleHint()){//getUserVisibleHint：获得Fragment的可见状态
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
        }
    }

    protected void submitTask(String cmd,String data){//使用线程池提交任务
        FutureTask<Integer> preTask = taskMap.get(cmd);
        if(preTask!=null){
            if(!preTask.isDone()){//之前的任务尚未完成,直接取消
                //不添加任务,先将前面未执行的相同任务完成
                return;
            }
        }
        //前一个任务不存在,或者已经完成
        byte[] result = BTProtocol.hexStringToByte(cmd,data);
        SendTask task = new SendTask(activity,result);
        FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
        taskMap.put(cmd,futureTask);
        executorService.submit(futureTask);
    }

    protected void sendDataByGPRS(String cmd){
        activity.sendDataByGPRS(cmd);
    }

    public void setShowArea(String text){
        if(text.startsWith("EB 90 00 ")){
            int start = 0;
            for(int i=0;i<3;i++){
                start = start+3;
            }
            text = text.substring(start,start+2);
            if(text.startsWith("8")){
                int cmd = Integer.parseInt(text.substring(1),16);
                FutureTask<Integer> futureTask = taskMap.get("0"+cmd);
                if(futureTask!=null && !futureTask.isDone()){
                    futureTask.cancel(true);
                }
                else{
                    MyLog.error(null,"没进去");
                }
                switch(CURRENT_FRAGMENT){
                    case 0:
                        updateHandAreaSucess(cmd);
                        break;
                }
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
                switch (CURRENT_FRAGMENT){
                    case 0:
                        updateHandAreaSucessState(cmd,num);
                        break;
                }
            }
        }
    }

    protected void updateHandAreaSucess(int cmd){

    }

    protected void updateHandAreaSucessState(String cmd,int num){

    }

    public int getCurrentFragment(){
        return CURRENT_FRAGMENT;
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
