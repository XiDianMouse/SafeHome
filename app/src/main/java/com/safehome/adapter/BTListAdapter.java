package com.safehome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.safehome.ItemEntry.BTItemEntry;
import com.safehome.ItemEntry.BTItemEntry.Status;
import com.safehome.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class BTListAdapter extends BaseAdapter{
	public static List<BTItemEntry> entrys;
	private HashSet<BTItemEntry> sets;
	private LayoutInflater mInflater;
	private BTItemListener mListener;
	private Context context;
	
	static class ViewHolder{
		ImageView icon;
		TextView name;
		TextView status;
		ProgressBar bar;
	}
	
	public interface BTItemListener{
		void OnItemClick(final BTItemEntry item);
	}

	public void setListener(BTItemListener listener) {
		this.mListener = listener;
	}
	public BTListAdapter(Context context){
		this.context = context;
		mInflater=(LayoutInflater)(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		entrys=new ArrayList<BTItemEntry>();
		sets = new HashSet<BTItemEntry>();
	}
	
	public void add(String name,String address,Status status,Object tag){	
		BTItemEntry entry=new BTItemEntry(entrys.size(),name,address,status);
		entry.setTag(tag);
		sets.add(entry);
		int length = entrys.size();
		if(sets.size()!=length)
			entrys.remove(length-1);
	}
	public void add(String name,String address,Object tag){
		add(name,address,Status.DISCONNECTED,tag);
	}
	
	public void destory(){
		entrys.clear();
		sets.clear();
		entrys=null;
		sets=null;
		mListener=null;
	}
	
	public void update(){
		this.notifyDataSetInvalidated();
	}
	
	public void update(String address,Status status){
		BTItemEntry entry = getSpecialEntry(address);
		if(entry!=null){
			entry.setStatus(status);
			this.notifyDataSetInvalidated();
		}
	}
	
	private BTItemEntry getSpecialEntry(String address){
		Iterator<BTItemEntry> it = sets.iterator();
		while(it.hasNext()){
			BTItemEntry entry = it.next();
			if(entry.getAddress().equals(address))
				return entry;
		}
		return null;
	}
	
	
	@Override
	public int getCount() {
		return entrys.size();
	}

	@Override
	public Object getItem(int position) {
		return entrys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.layout_item_bluetooth, parent, false);
			holder=new ViewHolder();
			holder.icon=(ImageView)convertView.findViewById(R.id.ic_item_bt);
			holder.name=((TextView)convertView.findViewById(R.id.name_item_bt));
			holder.status=((TextView)convertView.findViewById(R.id.status_item_bt));
			holder.bar=((ProgressBar)convertView.findViewById(R.id.bar_item_bt));
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		final BTItemEntry entry=entrys.get(position);
		holder.icon.setImageResource(entry.gerResId());
		holder.name.setText(entry.getName());
		int isVisible=View.INVISIBLE;
		String connected="";
		switch(entry.getStatus()){
			case DISCONNECTED://断开
				connected=context.getResources().getString(R.string.disconnected);
				break;
			case CONNECTING://正在连接
				isVisible=View.VISIBLE;
				connected=context.getResources().getString(R.string.connecting);
				break;
			case CONNECTED://连接
				connected=context.getResources().getString(R.string.connected);
				break;
		}
		holder.status.setText(connected);
		holder.bar.setVisibility(isVisible);
		convertView.findViewById(R.id.item_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(BTListAdapter.this.mListener!=null)
					BTListAdapter.this.mListener.OnItemClick(entry);
			}
		});
		return convertView;
	}
}
