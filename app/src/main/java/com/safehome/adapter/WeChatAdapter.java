package com.safehome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.safehome.R;
import com.safehome.adapter.WeChatAdapter.MyWeChatViewHolder;
import com.safehome.bean.wechat.WXItemBean;
import com.safehome.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @auther gbh
 * Created on 2017/5/17.
 */

public class WeChatAdapter extends RecyclerBaseAdapter<WXItemBean, MyWeChatViewHolder> {

    public WeChatAdapter(Context context){
        super(context);
    }

    static class MyWeChatViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_wechat_des)
        TextView tvWechatDes;
        @BindView(R.id.iv_wechat_pic)
        ImageView ivWechatPic;
        @BindView(R.id.tv_wechat_who)
        TextView tvWechatWho;
        @BindView(R.id.tv_wechat_time)
        TextView tvWechatTime;

        public MyWeChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    protected void bindDataToItemView(MyWeChatViewHolder viewHolder,final WXItemBean item) {
        GlideUtils.loadMovieTopImg(viewHolder.ivWechatPic,item.getPicUrl());
        viewHolder.tvWechatDes.setText(item.getTitle());
        viewHolder.tvWechatWho.setText(item.getDescription());
        viewHolder.tvWechatTime.setText(item.getCtime());
    }

    @Override
    public MyWeChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflateItemView(parent, R.layout.item_wechat);
        return new MyWeChatViewHolder(itemView);
    }
}
