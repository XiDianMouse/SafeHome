package com.safehome.gprs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.safehome.gprs.PhoneControl.Content;

public class SmsReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if(Content.SEND_SMS_ACTION.equals(intent.getAction()))
		{
			if(getResultCode()==Activity.RESULT_OK){//短信发送成功
				Toast.makeText(context, "短信发送成功", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(context, "短信发送失败", Toast.LENGTH_LONG).show();
			}
		}else if(Content.DELIVERED_SMS_ACTION.equals(intent.getAction()))
		{
			Toast.makeText(context, "收件人已成功接收", Toast.LENGTH_LONG).show();
		}
	}
}
