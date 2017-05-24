
package com.safehome.ItemEntry;

public class BTItemEntry{
	private String name;
	private String address;
	private Status status;//0:断开 1:连接 2:正在连接
	
	public enum Status{
		DISCONNECTED,
		CONNECTING,
		CONNECTED
	}
	
	public BTItemEntry(String name,String address, Status status) {
		this.name = name;
		this.address=address;
		this.status = status;
	}

	@Override
	public int hashCode(){
		return (this.name.hashCode())*(this.address.hashCode());
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

}

