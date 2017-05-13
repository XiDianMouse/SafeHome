package com.safehome.dialog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.safehome.ItemEntry.BTItemEntry;
import com.safehome.ItemEntry.BTItemEntry.Status;
import com.safehome.R;
import com.safehome.adapter.BTListAdapter;
import com.safehome.adapter.BTListAdapter.BTItemListener;
import com.safehome.bluetooth.BTClient;
import com.safehome.bluetooth.BTClient.BTEventListener;
import com.safehome.bluetooth.BTController;
import com.safehome.bluetooth.BTController.BTStateListener;
import com.safehome.ui.activity.main.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BTDialog implements BTStateListener,BTItemListener{
	private View dlgView;
	private BTListAdapter btAdapter;
	private List<BTClient> connectedClients;//记录已连接 的蓝牙端
	private AlertDialog dialog;
	private BTController btController;
	private Context context;
	
	public BTDialog(Context context) {
		connectedClients=new ArrayList<BTClient>();
		btController=BTController.getInstance(context);
		btController.setStateListener(this);
		btController.open();
		//设置显示界面
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dlgView=inflater.inflate(R.layout.layout_list_bluetooth,null);
		ListView lsView=(ListView)dlgView.findViewById(R.id.list_bt);
		btAdapter=new BTListAdapter(context);
		btAdapter.setListener(this);
		lsView.setAdapter(btAdapter);
		dialog=new AlertDialog.Builder(context).setTitle("设备连接").setView(dlgView).setNegativeButton("取消",new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).create();
		this.context = context;
	}

	public void showDialog(){
		addBTDevices(btController.getBondedDevices());//添加已绑定的设备
		btController.startDiscovery();
		dialog.show();
	}
	
	public void destory(){
		btAdapter.destory();
		dlgView=null;
		btController=null;
		btAdapter=null;
		connectedClients=null;
		dialog=null;
	}
	

	
	private void addBTDevices(Set<BluetoothDevice> btDevices){
		for(BluetoothDevice device:btDevices){
			btAdapter.add(device.getName(), device.getAddress(),device);
		}
		btAdapter.update();//更新显示列表
	}
	
	@Override
	public void OnItemClick(BTItemEntry entry) {
		BTClient client=btController.getClientByAddress(entry.getAddress());
		switch(entry.getStatus()){
			case DISCONNECTED://断开
				if(client==null){
					client=new BTClient((BluetoothDevice) entry.getTag());
					client.setReceivable(true);
					client.setEventListener(new MyBTEventHandler(entry.getAddress()));
				}
				switch(btController.connectBT(client)){
					case 0:
						entry.setStatus(Status.DISCONNECTED);
					case 1:
						entry.setStatus(Status.CONNECTED);
						connectedClients.add(client);
					case 2:
						entry.setStatus(Status.CONNECTING);
						break;
				}
				break;
			case CONNECTING://正在连接
			case CONNECTED://连接
				entry.setStatus(Status.DISCONNECTED);
				btController.disConnectBT(client);
				connectedClients.remove(client);
				break;
		}
		btAdapter.update();
	}

	class MyBTEventHandler implements BTEventListener{
		private String address;
		public MyBTEventHandler(String address) {
			this.address=address;
		}
		@Override
		public void onConnectFinished(boolean state) {
			if(state){
				btAdapter.update(address,Status.CONNECTED);
				connectedClients.add(btController.getClientByAddress(address));
				//更新蓝牙状态
				if(HomeActivity.msgHandler!=null){
					Message msg=HomeActivity.msgHandler.obtainMessage(HomeActivity.MSG_UPDATE_BT_STATE);
					msg.arg1=1;
					msg.sendToTarget();
				}
			}else{
				btAdapter.update(address,Status.DISCONNECTED);
			}
		}
		@Override
		public void onReceiveData(String address,byte[] data) {
			if(HomeActivity.msgHandler!=null){
				Message msg=HomeActivity.msgHandler.obtainMessage(HomeActivity.MSG_RECEIVE_DATA);
				msg.obj=new Object[]{address,data};
				msg.sendToTarget();
			}
		}
	}
	
	
	@Override
	public void onDiscoveryFinished(Set<BluetoothDevice> devices) {
		addBTDevices(devices);//添加搜索到 的设备
	}
	@Override
	public void onBTOpened() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onBTClosed() {
		// TODO Auto-generated method stub
		//更新蓝牙状态
		if(HomeActivity.msgHandler!=null){
			Message msg=HomeActivity.msgHandler.obtainMessage(HomeActivity.MSG_UPDATE_BT_STATE);
			msg.arg1=0;
			msg.sendToTarget();
		}
	}
}
