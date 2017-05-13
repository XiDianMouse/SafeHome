
package com.safehome.ItemEntry;

import com.safehome.R;
import com.safehome.adapter.BTListAdapter;

public class BTItemEntry{
	private int resId;
	private int id; 
	private String name;
	private String address;
	private Status status;//0:断开 1:连接 2:正在连接
	private Object tag;
	
	public enum Status{
		DISCONNECTED,
		CONNECTING,
		CONNECTED
	}
	
	public BTItemEntry(int id,int resId, String name,String address, Status status) {
		this.id=id;
		this.resId = resId;
		this.name = name;
		this.address=address;
		this.status = status;
	}
	
	public BTItemEntry(int id,String name,String address, Status status) {
		this(id,R.drawable.wheelchair_blue,name,address,status);
	}
	
	public BTItemEntry(int id,String name,String address) {
		this(id,R.drawable.wheelchair_blue,name,address, Status.DISCONNECTED);
	}
	
	@Override
	public int hashCode(){
		BTListAdapter.entrys.add(this);
		return 100;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj==null)
			return false;
		if(this==obj)
			return true;
		if(this.getClass()!=obj.getClass())
			return false;
		BTItemEntry entry = (BTItemEntry)obj;
		if(this.name.equals(entry.getName()) && this.address.equals(entry.getAddress()))
			return true;
		return false;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
	
	public int getId() {
		return id;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public int gerResId(){
		return resId;
	}
}

