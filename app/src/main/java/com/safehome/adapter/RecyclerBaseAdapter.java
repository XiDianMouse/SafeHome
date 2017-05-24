package com.safehome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safehome.listeners.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther gbh
 * Created on 2017/5/12.
 */

public abstract class RecyclerBaseAdapter<D,V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V>{
    protected final List<D> mDataList = new ArrayList<>();//RecyclerView中的数据集
    private OnItemClickListener<D> mItemClickListener;//点击事件回调处理
    protected Context mContext;

    public RecyclerBaseAdapter(Context context){
        mContext = context;
    }

    public void setItemClickListener(OnItemClickListener<D> itemClickListener){
        mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        final D item = getItem(position);
        bindDataToItemView(holder,item);
        setupItemViewClickListener(holder,item);
    }

    protected D getItem(int position){
        return mDataList.get(position);
    }

    protected abstract void bindDataToItemView(V viewHolder,D item);

    protected void setupItemViewClickListener(V viewHolder, final D item){
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener!=null){
                    mItemClickListener.onClick(item);
                }
            }
        });
    }

    public void addItems(List<D> items){
        items.removeAll(mDataList);//移除已经存在的数据
        mDataList.addAll(items);//添加新数据
        notifyDataSetChanged();
    }

    public void addItem(D item){
        mDataList.add(item);
        notifyDataSetChanged();
    }

    public List<D> getDataList(){
        return mDataList;
    }

    public void update(){
        this.notifyDataSetChanged();
    }

    public void clear(){
        mDataList.clear();
        notifyDataSetChanged();
    }

    protected View inflateItemView(ViewGroup viewGroup,int layoutId){
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutId,viewGroup,false);
    }
}
