package com.safehome.ui.fragment.gank;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @auther gbh
 * Created on 2017/5/12.
 */

public class AutoLoadRecyclerView extends RecyclerView{
    protected OnLoadListener mOnLoadListener;
    protected boolean mIsLoading = false;
    protected boolean mIsValidDelay = true;

    public interface OnLoadListener{
        void onLoad();
    }

    public void setOnLoadListener(OnLoadListener listener){
        mOnLoadListener = listener;
    }

    public void setLoading(boolean loading){
        mIsLoading = loading;
    }

    public AutoLoadRecyclerView(Context context){
        this(context,null);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public AutoLoadRecyclerView(Context context,AttributeSet attrs,int defSytle){
        super(context,attrs,defSytle);
        //isInEditMode():解决可视化编辑器无法识别自定义控件的问题
        if(isInEditMode()){
            return;
        }
        //通过View的滑动属性来判断,是否在顶部,或者在底部。
        addOnScrollListener(new OnScrollListener() {
            /*
            * dx，dy分别表示 在x方向和y方向滑动的值,这个值有正负,通过这两个参数就可以监听滑动方向状态,
            * 但是还有两种状态不能通知dx和dy直接判断出来:顶部top状态和底部bottom状态。
            * */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                checkLoadMore(dx,dy);
            }
        });
    }

    private void checkLoadMore(int dx,int dy){
        if(isBottom(dx,dy)  && !mIsLoading && mIsValidDelay && mOnLoadListener!=null){
            mIsValidDelay = false;
            mOnLoadListener.onLoad();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsValidDelay = true;
                }
            },1000);
        }
    }

    private boolean isBottom(int dx,int dy){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)getLayoutManager();
        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        int totalItemCount = linearLayoutManager.getItemCount();
        return lastVisibleItem>=totalItemCount-4 && dy>0;
    }
}
