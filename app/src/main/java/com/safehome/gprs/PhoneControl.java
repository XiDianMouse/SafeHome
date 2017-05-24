package com.safehome.gprs;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.safehome.ui.activity.main.HomeActivity;

import java.util.ArrayList;

public class PhoneControl {
	public static PhoneControl instance;
	private HomeActivity mContext;
	private PendingIntent mSmsSendPI;
	private PendingIntent mSmsDeliverPI;
	private BroadcastReceiver mSmsReceiver;
	
	public interface Content
	{
		String SEND_SMS_ACTION ="send.sms.action";
		String DELIVERED_SMS_ACTION = "delivered.sms.action";
	}
	
	private PhoneControl(HomeActivity context)
	{
		this.mContext = context;
		mSmsSendPI = PendingIntent.getBroadcast(context,0,new Intent(Content.SEND_SMS_ACTION),0);
		mSmsDeliverPI = PendingIntent.getBroadcast(context,0,new Intent(Content.DELIVERED_SMS_ACTION),0);
		mSmsReceiver = new SmsReceiver();
		registSmsReceiver();
	}
	
	public static PhoneControl getInstance(HomeActivity context)
	{
		if(instance==null)
		{
			instance = new PhoneControl(context);
		}
		return instance;
	}
	
	public static PhoneControl getInstance()
	{
		return instance;
	}
	
	private void registSmsReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Content.SEND_SMS_ACTION);
		filter.addAction(Content.DELIVERED_SMS_ACTION);
		mContext.registerReceiver(mSmsReceiver,filter);
	}
	
	public void sendMessage(String phoneNumber,String message)
	{
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> msgs = smsManager.divideMessage(message);
		for(String msg:msgs)
		{
			smsManager.sendTextMessage(phoneNumber,null,msg,mSmsSendPI,mSmsDeliverPI);
		}
	}
}
