package com.safehome.ui.activity.base;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;

import com.safehome.R;
import com.safehome.http.LifeSubscription;

import java.util.LinkedList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @auther gbh
 * Created on 2017/5/5.
 */

public abstract class BaseActivity extends AppCompatActivity implements LifeSubscription{
    //管理运行的所有的Activity
    public final static List<AppCompatActivity> mActivities = new LinkedList<>();
    public static BaseActivity activity;
    //从左边滑动到右边关闭的变量
    private int endX;
    private int startX;
    private int deltaX;
    private int endY;
    private int startY;
    private int deltaY;

    private View decorView;
    private int mScreenLength;
    private VelocityTracker mVelocityTracker;
    private boolean isClose = true;

    @Override
    protected void onResume(){
        super.onResume();
        activity = this;
    }
    @Override
    protected void onPause(){
        super.onPause();
        activity = null;
    }

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        mVelocityTracker = VelocityTracker.obtain();
        /*
        * 一、DecorView为整个Window界面的最顶层View。
        * 二、DecorView只有一个子元素为LinearLayout。代表整个Window界面，包含通知栏，标题栏，内容显示栏三块区域。
        * 三、LinearLayout里有两个FrameLayout子元素。
        *  3.1、标题栏显示界面。只有一个TextView显示应用的名称。也可以自定义标题栏，载入后的自定义标题栏View将加入FrameLayout中。
        *  3.2、为内容栏显示界面。就是setContentView()方法载入的布局界面，加入其中。
        * */
        decorView = getWindow().getDecorView();
        mScreenLength = decorView.getWidth();
        synchronized (mActivities){
            mActivities.add(this);
        }
    }

    protected void setToolBar(Toolbar toolbar, String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示toolbar的返回按钮
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private CompositeSubscription mCompositeSubscription;

    //用于添加rx的监听的在onDestroy中记得关闭不然会内存泄漏
    @Override
    public void bindSubscription(Subscription subscription){
        if(mCompositeSubscription==null){
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public void killAll(){
        List<AppCompatActivity> copy;
        synchronized (mActivities){
            copy = new LinkedList<AppCompatActivity>();
        }
        for(AppCompatActivity activity:copy){
            activity.finish();
        }
        //杀死当前进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void tochFinish(){
        super.finish();
        overridePendingTransition(R.anim.alpha_enter,R.anim.alpha_exit);
    }

    public void closeAnimator(int deltaX){
        if(isClose){
            /*
            * 一般使用ValueAnimator实现动画分为以下七个步骤：
              1. 调用ValueAnimation类中的ofInt(int...values)、ofFloat(String propertyName,float...values)等静态方法实例化ValueAnimator对象，并设置目标属性的属性名、初始值或结束值等值;
              2.调用addUpdateListener(AnimatorUpdateListener mListener)方法为ValueAnimator对象设置属性变化的监听器;
              3.创建自定义的Interpolator，调用setInterpolator(TimeInterpolator value)为ValueAniamtor设置自定义的Interpolator;(可选，不设置默认为缺省值)
              4.创建自定义的TypeEvaluator,调用setEvaluator(TypeEvaluator value)为ValueAnimator设置自定义的TypeEvaluator;(可选，不设置默认为缺省值)
              5.在AnimatorUpdateListener 中的实现方法为目标对象的属性设置计算好的属性值。
              6.设置动画的持续时间、是否重复及重复次数等属性;
              7.为ValueAnimator设置目标对象并开始执行动画。
            * */
            ValueAnimator animator = ValueAnimator.ofInt(deltaX,mScreenLength);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
                @Override
                public void onAnimationUpdate(ValueAnimator animation){//动画每播放一帧时调用
                    /*
                    * 在动画过程中，可侦听此事件来获取并使用 ValueAnimator 计算出来的属性值。利用传入事件的
                    * ValueAnimator 对象，调用其 getAnimatedValue() 方法即可获取当前的属性值。如果使用
                    * ValueAnimator来实现动画的话 ，则必需实现此侦听器。
                    * */
                    int value = (Integer)animation.getAnimatedValue();
                    decorView.scrollTo(-value,0);
                }
            });
            animator.addListener(new Animator.AnimatorListener(){
                @Override
                public void onAnimationStart(Animator arg0){//动画开始时调用

                }
                @Override
                public void onAnimationRepeat(Animator arg0){//动画循环播放时调用

                }
                @Override
                public void onAnimationEnd(Animator arg0){//动画结束时调用
                    if(isClose){
                        tochFinish();
                    }
                }
                @Override
                public void onAnimationCancel(Animator arg0){//动画被取消时调用

                }
            });
            animator.setDuration(300);
            animator.start();
        }else{
            ValueAnimator animator = ValueAnimator.ofInt(deltaX,0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
                @Override
                public void onAnimationUpdate(ValueAnimator animation){
                    int value = (Integer)animation.getAnimatedValue();
                    decorView.scrollTo(-value,0);
                }
            });
            animator.setDuration(300);
            animator.start();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        /*
        * android.view.VelocityTracker主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率。
        * 用addMovement(MotionEvent)函数将Motion event加入到VelocityTracker类实例中.你可以使用getXVelocity()
        * 或getXVelocity()获得横向和竖向的速率到速率时，但是使用它们之前请先调用computeCurrentVelocity(int)来初
        * 始化速率的单位 。
        * */
        mVelocityTracker.addMovement(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = (int)ev.getRawX();
                startY = (int)ev.getRawY();
                if(startX<mScreenLength/32){
                    return true;
                }else{
                    return super.dispatchTouchEvent(ev);
                }
            case MotionEvent.ACTION_MOVE:
                endX = (int)ev.getRawX();
                endY = (int)ev.getRawY();
                deltaX = endX-startX;
                deltaY = endY-startY;
                if(deltaX>deltaY && startX<mScreenLength/32){
                    decorView.scrollTo(-deltaX,0);
                    decorView.getBackground().
                            setColorFilter((Integer)evaluateColor((float)deltaX/(float)mScreenLength,Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
                    return true;
                }else{
                    return super.dispatchTouchEvent(ev);
                }
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (-25 < xVelocity && xVelocity <= 50 && deltaX > mScreenLength / 3 && startX < mScreenLength / 32
                        || xVelocity > 50 && startX < mScreenLength / 32) {
                    isClose = true;
                    closeAnimator(deltaX);
                    return true;
                } else {
                    if (deltaX > 0 && startX < mScreenLength / 32) {
                        isClose = false;
                        closeAnimator(deltaX);
                        return true;
                    } else {
                        if (startX < mScreenLength / 32) {
                            decorView.scrollTo(0, 0);
                        }
                        return super.dispatchTouchEvent(ev);
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                return super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24 |
                (startR + (int) (fraction * (endR - startR))) << 16|
                (startG + (int) (fraction * (endG - startG))) << 8 |
                (startB + (int) (fraction * (endB - startB)));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        synchronized (mActivities){
            mActivities.remove(this);
        }
    }
}
