package com.safehome.protocol;

import java.util.ArrayList;

public class BTProtocol {
	//  Packet format.
	//    0 bytes  1        2        3        4        LEN+4
	//    +--------+--------+--------+--------+ +--------+--------+ +--------+
	//    |       SOF       |   LEN  |  CMD   | |     DATA....    | | CHECK  |
	//    +--------+--------+--------+--------+ +--------+--------+ +--------+
	public static String sofString = "EB90";
	public static String lengthStr;
	public static String cmdStr;
	public static String dataStr;
	public static String checkStr;
	
	public static String getStr(String cmd,String data){
		int dataLength = 2;
		if(cmd!=null){
			String dataLengthStr = Integer.toHexString(dataLength);
			int length = 2-dataLengthStr.length();
			for(int i=0;i<length;i++){
				dataLengthStr = "0"+dataLengthStr;
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(sofString);
			stringBuilder.append(dataLengthStr);
			stringBuilder.append(cmd);
			stringBuilder.append(data);
			return getHexStr(stringBuilder);
		}
		return null;
	}
	
	private static String getHexStr(StringBuilder stringBuilder){
		String str = stringBuilder.toString();
		int check = 0;
		ArrayList<Integer> dataList = new ArrayList<>();
		int start = 0;
		int groupLength = str.length()/2;
		for(int i=0;i<groupLength;i++){
		    dataList.add(Integer.valueOf(str.substring(start,start+2),16));
			start = start+2;
		}
		for(int i=3;i<groupLength;i++){
			check = check^dataList.get(i);
		}
		String checkStr = Integer.toHexString(check);
		int length = 2-checkStr.length();
		for(int i=0;i<length;i++){
			checkStr = "0"+check;
		}
		checkStr = checkStr.toUpperCase();
		return str+checkStr;
	}
}
