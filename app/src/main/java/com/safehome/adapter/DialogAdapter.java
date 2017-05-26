package com.safehome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.safehome.ItemEntry.BTItemEntry;
import com.safehome.R;
import com.safehome.adapter.DialogAdapter.DialogViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @auther gbh
 * Created on 2017/5/21.
 */

public class DialogAdapter extends RecyclerBaseAdapter<BTItemEntry, DialogViewHolder> {
	//我觉得这样做比较好 ---------------------- 2017.5.26
    public DialogAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindDataToItemView(DialogViewHolder viewHolder, BTItemEntry item) {
        viewHolder.itemName.setText(item.getName());
        int isVisible = View.INVISIBLE;
        switch (item.getStatus()){
            case DISCONNECTED:
                viewHolder.itemStatus.setText(R.string.disconnected);
                break;
            case CONNECTING:
                isVisible = View.VISIBLE;
                viewHolder.itemStatus.setText(R.string.connecting);
                break;
            case CONNECTED:
                viewHolder.itemStatus.setText(R.string.connected);
                break;
        }
        viewHolder.itemProgress.setVisibility(isVisible);
    }

    @Override
    public DialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflateItemView(parent, R.layout.item_dialog);
        return new DialogViewHolder(itemView);
    }

    static class DialogViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_status)
        TextView itemStatus;
        @BindView(R.id.item_progress)
        ProgressBar itemProgress;

        public DialogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
