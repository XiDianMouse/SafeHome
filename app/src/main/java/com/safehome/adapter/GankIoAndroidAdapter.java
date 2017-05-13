package com.safehome.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.safehome.R;
import com.safehome.adapter.GankIoAndroidAdapter.MyAndroidViewHolder;
import com.safehome.bean.gankio.GankIoDataBean;
import com.safehome.utils.GlideUtils;
import com.safehome.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class GankIoAndroidAdapter extends RecyclerBaseAdapter<GankIoDataBean.ResultBean,MyAndroidViewHolder>{

    static class MyAndroidViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_android_des)
        TextView tvAndroidDes;
        @BindView(R.id.iv_android_pic)
        ImageView ivAndroidPic;
        @BindView(R.id.tv_android_who)
        TextView tvAndroidWho;
        @BindView(R.id.tv_android_time)
        TextView tvAndroidTime;

        public MyAndroidViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    protected void bindDataToItemView(MyAndroidViewHolder viewHolder, GankIoDataBean.ResultBean item) {
        if (item.getImages() != null && item.getImages().size() > 0 && !TextUtils.isEmpty(item.getImages().get(0))) {
            viewHolder.ivAndroidPic.setVisibility(View.VISIBLE);
            GlideUtils.loadMovieTopImg(viewHolder.ivAndroidPic, item.getImages().get(0));
        } else {
            viewHolder.ivAndroidPic.setVisibility(View.GONE);
        }
        viewHolder.tvAndroidDes.setText(item.getDesc());
        viewHolder.tvAndroidWho.setText(item.getWho());
        viewHolder.tvAndroidTime.setText(TimeUtil.getTranslateTime(item.getPublishedAt()));
    }

    @Override
    public MyAndroidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflateItemView(parent,R.layout.item_android);
        return new MyAndroidViewHolder(itemView);
    }
}