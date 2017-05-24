package com.safehome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.safehome.R;
import com.safehome.adapter.MenuAdapter.MenuViewHolder;
import com.safehome.bean.menu.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @auther gbh
 * Created on 2017/5/12.
 */

public class MenuAdapter extends RecyclerBaseAdapter<MenuItem, MenuViewHolder> {
    public MenuAdapter(Context context){
        super(context);
    }

    @Override
    protected void bindDataToItemView(MenuViewHolder viewHolder, MenuItem item) {
        viewHolder.menuIv.setImageResource(item.iconResId);
        viewHolder.menuTv.setText(item.text);
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflateItemView(parent, R.layout.item_menu);
        return new MenuViewHolder(itemView);
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_iv)
        ImageView menuIv;
        @BindView(R.id.menu_tv)
        TextView menuTv;

        public MenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
