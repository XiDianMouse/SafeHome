package com.safehome.ItemEntry;

public class FunctionItem {
	private int id;
	private String name;
	
	public FunctionItem(int id,String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
}
