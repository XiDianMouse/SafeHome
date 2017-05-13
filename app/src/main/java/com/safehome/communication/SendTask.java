package com.safehome.communication;

import android.os.Message;
import android.util.Log;

import com.safehome.ui.activity.main.HomeActivity;

import java.util.concurrent.Callable;

/**
 * @auther gbh
 * Created on 2017/5/2.
 */

public class SendTask implements Callable<Integer> {
    private int maxRunTimes;//最大等待次数,每次等待10ms
    private byte[] result;
    private HomeActivity activity;

    public SendTask(HomeActivity activity, byte[] result){
        this.activity = activity;
        maxRunTimes = 50;
        this.result = result;
    }

    @Override
    public Integer call() throws Exception{
        boolean falg = activity.sendData(result);//true:发送成功 false:发送失败
        //是否能发出数据
        if(!falg && HomeActivity.msgHandler!=null){
            Message msg=HomeActivity.msgHandler.obtainMessage(HomeActivity.MSG_SEND_HANDCMD_FAIL);
            msg.sendToTarget();
        }
        //是否能接收到反馈数据
        while(maxRunTimes>0){//50*10ms
            try{
                Thread.sleep(10);
            }
            catch(InterruptedException e){
                /***********************************FutureTask的cancel方法并不能停止掉一个正在执行的异步任务
                 * “当抛出异常或是该任务调用Thread.interrupted(),中断状态会被复位”也就是说，我们在调用
                 * Thread.currentThread().isInterrupted()或者是Thread.interrupted()得到的都是false，
                 * 所以用我上述的方法来做为while循环结束的标识不好，当然也不建议让异常机制来控制执行流程，所以建议用一个标志位来跳出循环
                 *
                 *	InterruptedException - 如果在当前线程等待通知之前或者正在等待通知时，另一个线程中断了当前线程。
                 *	在抛出此异常时，当前线程的中断状态 被清除。也就是说，从wait中断后，中断标记就被清掉了，while里的
                 *	Thread.currentThread().isInterrupted()那个条件自然一直返回false
                 *	于是不能使用while(Thread.currentThread().isInterrupted()&&maxRunTimes>0)
                 *
                 *
                 *****************解决方案*****************************
                 * Future cancle(true) 最后调用的其实还是执行线程的r.interrupt()设置线程的中断状态;
                 * 设置完以后恒返回true;其实他从某种角度来说不关心线程是否真的停止了;(可以参考源码)这样当你sleep
                 * 的时候就可以捕获到这个InterruptedException异常!而你的写法在打印异常却没有做任何的退出操作;
                 *	修改方法一:
                 *	在捕获异常处break
                 *	修改方法二:
                 *	捕获异常后再次设置中断状态,循环条件处判断中断状态
                 *
                 * */
                Log.e(null,"进入异常");
                break;
            }
            maxRunTimes--;
        }
        if(maxRunTimes==0){//未收到反馈数据
            if(HomeActivity.msgHandler!=null){
                Message msg=HomeActivity.msgHandler.obtainMessage(HomeActivity.MSG_HANDCMD_UNRESPONSE);
                msg.sendToTarget();
            }
        }
        return 0;
    }
}
