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
	
	private static String getStr(String cmd,String data){
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
		ArrayList<Integer> dataList = new ArrayList<Integer>();
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
	
	public static byte[] hexStringToByte(String cmd,String data)//将16进制字符串转换为byte[]数组
	{ 
		String hex = getStr(cmd,data);
        int len = (hex.length()/2);  
        byte[] result = new byte[len];  
        char[] achar = hex.toCharArray();  
        for(int i=0;i<len;i++){  
            int pos = i*2;  
            result[i] = (byte)(toByte(achar[pos])<<4|toByte(achar[pos+1]));  
        }  
        return result;  
    }  
  
	public static byte toByte(char c)
	{  
	    byte b = (byte)"0123456789ABCDEF".indexOf(c);  
	    return b;  
	}
	
	//将字节数组转为16进制字符串
	public static String toHex(byte[] buffer) {
	  String ret="error!!!";
      StringBuffer sb = new StringBuffer();
      try{
	      for (int i = 0; i < buffer.length; i++) {
	       sb.append(Character.forDigit((buffer[i]>>4)&0x0F, 16));
	       sb.append(Character.forDigit(buffer[i]&0x0F, 16));
	       sb.append(" ");
	      }
	      ret=sb.toString().toUpperCase();
      }catch(Exception e){
    	  
      }
      return ret;
   }
}
