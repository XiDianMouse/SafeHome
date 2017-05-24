package com.safehome.ui.fragment.home.child;

import android.bluetooth.BluetoothDevice;
import android.view.View;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.ItemEntry.BTItemEntry;
import com.safehome.R;
import com.safehome.adapter.DialogAdapter;
import com.safehome.bluetooth.BTClient;
import com.safehome.bluetooth.BTController;
import com.safehome.dialog.BluetoothDialog;
import com.safehome.listeners.OnItemClickListener;
import com.safehome.tcp.TcpController;

import java.util.Set;

import butterknife.OnClick;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class PageConnectFragment extends BaseHomeFragment implements BTController.BTStateListener,OnItemClickListener<BTItemEntry>{
    private DialogAdapter mDialogAdapter;

    public PageConnectFragment() {
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.layout_fragment_connect;
    }

    @Override
    protected void initWidgets() {
        mBluetoothDialog = new BluetoothDialog(activity);
        mDialogAdapter = mBluetoothDialog.getDialogAdapter();
        mDialogAdapter.setItemClickListener(this);
        mBTController.setStateListener(this);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {//do things when fragment is visible
        } else {
        }
    }

    @OnClick({R.id.select_bluetoothConnect, R.id.select_gprsConnect, R.id.select_httpConnect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_bluetoothConnect:
                mCommandStyle = 0;
                showBluetoothDialog();
                ToastUtils.showShortToast("通过蓝牙下发");
                break;
            case R.id.select_gprsConnect:
                mCommandStyle = 1;
                ToastUtils.showShortToast("通过GPRS下发");
                break;
            case R.id.select_httpConnect:
                mCommandStyle = 2;
                connectToServer();
                ToastUtils.showShortToast("通过公网连接");
                break;
        }
    }

    private void showBluetoothDialog(){
        mBluetoothDialog.addDevices(mBTController.getBondedDevices());
        mBTController.startDiscovery();
        mBluetoothDialog.showDialog();
    }

    private void connectToServer(){
        if(mTcpController==null) {
            mTcpController = TcpController.getInstance();
        }
        new Thread(mTcpController).start();
    }

    @Override
    public void onBTOpened(){

    }

    @Override
    public void onBTClosed(){

    }

    @Override
    public void onDiscoveryFinished(Set<BluetoothDevice> devices){
        mBluetoothDialog.addDevices(devices);
    }

    @Override
    public void onClick(BTItemEntry item){
        BTClient btClient = mBTController.getClientByAddress(item.getAddress());
        switch(item.getStatus()){
            case DISCONNECTED:
                if(btClient==null){
                    btClient = new BTClient(mBluetoothDialog.getBluetoothDevice(item.getAddress()));
                    btClient.setReceivable(true);
                    btClient.setBTClientStateListener(new BTClientListener(item.getAddress()));
                }
                switch (mBTController.connectBT(btClient)){
                    case 0:
                        item.setStatus(BTItemEntry.Status.DISCONNECTED);
                        break;
                    case 1:
                        item.setStatus(BTItemEntry.Status.CONNECTED);
                        break;
                    case 2:
                        item.setStatus(BTItemEntry.Status.CONNECTING);
                        break;
                }
                break;
            case CONNECTING:
                break;
            case CONNECTED:
                item.setStatus(BTItemEntry.Status.DISCONNECTED);
                mBTController.disConnectBT(btClient);
                break;
        }
        mDialogAdapter.update();
    }

    class BTClientListener implements BTClient.BTClientStateListener{
        private String address;
        public BTClientListener(String addr){
            this.address = addr;
        }

        @Override
        public void onConnectFinished(boolean state){
            mIsConnected = state;
            if(state){
                ToastUtils.showShortToast("已连接");
                mBluetoothDialog.updateBTState(address, BTItemEntry.Status.CONNECTED);
            }else{
                ToastUtils.showShortToast("已断开");
                mBluetoothDialog.updateBTState(address, BTItemEntry.Status.DISCONNECTED);
            }
        }

//        @Override
//        public void onReceiveData(String address,byte[] data){
//            String strData = com.safehome.utils.StringUtils.toHex(data);
//            switch(mCurrentFragment){
//                case 0://手指
//                    mPageHandFragment.setShowArea(receiveData);
//                    break;
//                case 1://人脸
//                    break;
//            }
//            ToastUtils.showShortToast("蓝牙收到的数据为:"+strData);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
