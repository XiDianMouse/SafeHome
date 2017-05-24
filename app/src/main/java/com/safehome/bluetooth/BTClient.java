package com.safehome.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.CountDownTimer;

import com.safehome.listeners.OnReceiverListener;
import com.safehome.utils.MyLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;

public class BTClient {
	public static final String TAG="BTClient";
	private static final String SPP_UUID="00001101-0000-1000-8000-00805F9B34FB";
	private BluetoothDevice btDevice;
	private BluetoothSocket btSocket;
//	private byte[] buffer=new byte[512];
	private Vector<Byte> buffer=new Vector<Byte>(512);
	private boolean isConnected;
	private boolean isReceivable;
	private InputStream inStream;
	private OutputStream outStream;
	private BTClientStateListener stateListener;
	private OnReceiverListener<String> onReceiverListener;
	
	public interface BTClientStateListener{
		void onConnectFinished(boolean state);
	}
	public BTClient(BluetoothDevice device){
		btDevice=device;
	}
	
	public void setBTClientStateListener(BTClientStateListener listener){
		stateListener=listener;
	}

	public void setOnReceiverListener(OnReceiverListener listener){
		onReceiverListener = listener;
	}
	
	public BluetoothDevice getDevice() {
		return btDevice;
	}
	
	public boolean isReceivable() {
		return isReceivable;
	}
	public void setReceivable(boolean isReceivable) {
		this.isReceivable = isReceivable;
	}
	
	protected int connect(){
		if(isConnected)
			return 1;
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(!isCountDownTimerEnabled){
						isCountDownTimerEnabled=true;
						mCountDownTimer.start();
					}
					try {
						btSocket=btDevice.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
						inStream=btSocket.getInputStream();
						outStream=btSocket.getOutputStream();
						btSocket.connect();
						isConnected=btSocket.isConnected();
						startReceiveData();
					} catch (IOException e) {

						MyLog.error(TAG, "connect():"+e.getMessage());
					}
					MyLog.error(TAG, "isConnected:"+isConnected);
				}
			}).start();
			return 2;
		} catch (Exception e) {
			MyLog.error(TAG, "connect():"+e.getMessage());
		}
		return 0;
	}
	
	protected void disconnect(){
		if(isCountDownTimerEnabled){
			isCountDownTimerEnabled=false;
			mCountDownTimer.cancel();
		}
		try {
			isConnected=false;
			btSocket.close();
			inStream.close();
			outStream.close();
		} catch (IOException e) {
			MyLog.error(TAG, "disconnect():"+e.getMessage());
		}
	}
	
	
	public boolean isConnected(){
		return isConnected;
	}
	
	private void startReceiveData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				int ret=0;
				while(isConnected && isReceivable){
					try {
						if((ret=inStream.read())!=-1){
							if(!isTimerEnabled){
								isTimerEnabled=true;
								mTimer.start();
							}
							buffer.add((byte)ret);
						}
					} catch (IOException e) {
						MyLog.error(TAG, "startReceiveData():"+e.getMessage());
						break;
					}
				}
			}
		}).start();
	}
	
	public void sendData(byte[] data){
		if(isConnected){
			try {
				outStream.write(data);
			} catch (IOException e) {
				MyLog.error(TAG, "sendData():"+e.getMessage());
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(o instanceof BTClient){
			BTClient c=(BTClient)o;
			if(btDevice==null||c.getDevice()==null)
				return true;
			return btDevice.getAddress().equals(c.getDevice().getAddress());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (btDevice==null)? this.getClass().getName().hashCode()
				: this.btDevice.getAddress().hashCode();
	}
	
	
	
	private boolean isCountDownTimerEnabled;
	private CountDownTimer mCountDownTimer=new CountDownTimer(10000,1000) {//10s
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			if(isCountDownTimerEnabled && isConnected){
				isCountDownTimerEnabled=false;
				if(stateListener!=null)
					stateListener.onConnectFinished(true);
			}
		}
		
		@Override
		public void onFinish() {
			if(isCountDownTimerEnabled){
				isCountDownTimerEnabled=false;
				if(stateListener!=null)
					stateListener.onConnectFinished(isConnected);
			}
		}
	};
	
	
	private boolean isTimerEnabled;
	private CountDownTimer mTimer=new CountDownTimer(200,200) {//200ms
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onFinish() {
			synchronized (BTClient.this) {
				isTimerEnabled=false;
				if(onReceiverListener!=null){
					byte[] arr=new byte[buffer.size()];
					for(int i=0;i<arr.length;i++){
						arr[i]=buffer.get(i);
					}
					onReceiverListener.onReceive(com.safehome.utils.StringUtils.toHex(arr),btDevice.getAddress());
				}
				buffer.clear();
			}
		}
	};
}
