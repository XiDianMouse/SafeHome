package com.safehome.utils;

/**
 * @auther gbh
 * Created on 2017/5/20.
 */

public class StringUtils {
    public static byte[] hexStringToByte(String data)//将16进制字符串转换为byte[]数组
    {
        int len = (data.length()/2);
        byte[] result = new byte[len];
        char[] achar = data.toCharArray();
        for(int i=0;i<len;i++){
            int pos = i*2;
            result[i] = (byte)(toByte(achar[pos])<<4|toByte(achar[pos+1]));
        }
        return result;
    }

    private static byte toByte(char c)
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
