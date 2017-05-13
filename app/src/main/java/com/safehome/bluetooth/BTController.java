package com.safehome.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BTController {
	private Context mContext;
	private BluetoothAdapter btAdapter;
	private Set<BluetoothDevice> foundedDevices;
	private List<BTClient> connectedClients;
	private Map<String,BTClient> clients;
	private BTStateListener btListener;
	private static BTController instance;

	public interface BTStateListener{
		void onBTOpened();
		void onBTClosed();
		void onDiscoveryFinished(Set<BluetoothDevice> devices);
	}

	private BTController(Context context){
		mContext=context;
		foundedDevices=new HashSet<BluetoothDevice>();
		clients=new HashMap<String, BTClient>();
		connectedClients=new ArrayList<BTClient>();
	}

	public static BTController getInstance(Context context){
		if(instance==null){
			instance=new BTController(context);
		}
		return instance;
	}

	public void setStateListener(BTStateListener listener){
		btListener=listener;
	}

	public void sendData(byte[] data){
		for(BTClient client:getConnectedClients()){
			client.sendData(data);
		}
	}

	public void sendData(BTClient client, byte[] data){
		if(client!=null && client.isConnected()){
			client.sendData(data);
		}
	}

	public boolean isDeviceConnected(String deviceAddress){
		BTClient client;
		if((client=getClientByAddress(deviceAddress))!=null){
			return client.isConnected();
		}
		return false;
	}

	public BTClient getClientByAddress(String deviceAddress){
		return clients.get(deviceAddress);
	}
	public List<BTClient> getConnectedClients(){
		connectedClients.clear();
		Set<String> keys=clients.keySet();
		BTClient client;
		for(String key:keys){
			client=clients.get(key);
			if(client.isConnected()){
				connectedClients.add(client);
			}
		}
		return connectedClients;
	}

	public void open() {
		foundedDevices.clear();
		clients.clear();
		btAdapter=BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter=new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(btReceiver, filter);
		if(btAdapter!=null){
			btAdapter.enable();
		}
	}

	public void close() {
		for(String key: clients.keySet()){
			clients.get(key).disconnect();
		}
		mContext.unregisterReceiver(btReceiver);
		if(btAdapter!=null){
			btAdapter.disable();
		}
	}

	public Set<BluetoothDevice> getBondedDevices(){
		return (btAdapter==null) ? new HashSet<BluetoothDevice>() : btAdapter.getBondedDevices();
	}

	public void startDiscovery(){
		if(btAdapter!=null && btAdapter.isEnabled()){
			if(!btAdapter.isDiscovering()){
				btAdapter.startDiscovery();
			}
		}
	}


	public int connectBT(BTClient client){
		if(client==null||client.getDevice()==null)
			return 0;
		if(btAdapter==null || !btAdapter.isEnabled())
			return 0;
		if(btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();
		if(!clients.containsKey(client.getDevice().getAddress()))
			clients.put(client.getDevice().getAddress(),client);
		int ret=client.connect();
		if(ret==1){
			return 1;
		}else if(ret==2){
			return 2;
		}
		return 0;
	}

	public void disConnectBT(BTClient client){
		if(client!=null)
			client.disconnect();
	}
	
	private BroadcastReceiver btReceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
				int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
				int prevState=intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
				if(prevState==BluetoothAdapter.STATE_OFF
						&& state==BluetoothAdapter.STATE_TURNING_ON){//打开蓝牙
					foundedDevices.clear();
					clients.clear();
				}else if(prevState==BluetoothAdapter.STATE_TURNING_ON
						&& state==BluetoothAdapter.STATE_ON){//蓝牙已打开
					if(btListener!=null){
						btListener.onBTOpened();
					}
				}else if(state==BluetoothAdapter.STATE_OFF){//关闭蓝牙
					for(String key: clients.keySet()){
						clients.get(key).disconnect();
					}
					if(btListener!=null){
						btListener.onBTClosed();
					}
				}
			}else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
				BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getBondState() != BluetoothDevice.BOND_BONDED){
					foundedDevices.add(device);
				}
			}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
				if(btListener!=null){
					btListener.onDiscoveryFinished(foundedDevices);
				}
			}
		}
	};
	
	
}
