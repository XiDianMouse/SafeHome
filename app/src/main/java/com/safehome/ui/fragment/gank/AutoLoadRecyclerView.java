package com.safehome.ui.fragment.gank;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @auther gbh
 * Created on 2017/5/12.
 */

public class AutoLoadRecyclerView extends RecyclerView{
    public AutoLoadRecyclerView(Context context){
        this(context,null);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public AutoLoadRecyclerView(Context context,AttributeSet attrs,int defSytle){
        super(context,attrs,defSytle);
    }
}
