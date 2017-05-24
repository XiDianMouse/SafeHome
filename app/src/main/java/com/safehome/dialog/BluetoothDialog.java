package com.safehome.dialog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.safehome.ItemEntry.BTItemEntry;
import com.safehome.R;
import com.safehome.adapter.DialogAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @auther gbh
 * Created on 2017/5/21.
 */

public class BluetoothDialog{
    private HashMap<String,BluetoothDevice> devicesMap;
    private View mDlgView;
    private RecyclerView dialogRecyclerview;
    private AlertDialog mAlertDialog;
    private DialogAdapter mDialogAdapter;

    public BluetoothDialog(Context context) {
        devicesMap = new HashMap<>();
        mDlgView = LayoutInflater.from(context).inflate(R.layout.layout_dialog, null);
        dialogRecyclerview = (RecyclerView)mDlgView.findViewById(R.id.dialog_recyclerview);
        dialogRecyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        mDialogAdapter = new DialogAdapter(context);
        dialogRecyclerview.setAdapter(mDialogAdapter);
        mAlertDialog = new AlertDialog.Builder(context)
                .setTitle("设备连接")
                .setView(mDlgView)
                .setNegativeButton("取消",new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which){
                        mAlertDialog.cancel();
                    }
                }).create();
    }

    public void showDialog(){
        mAlertDialog.show();
    }

    public DialogAdapter getDialogAdapter(){
        return mDialogAdapter;
    }

    public void addDevices(Set<BluetoothDevice> set){
        for(BluetoothDevice device:set) {
            BTItemEntry item = new BTItemEntry(device.getName(), device.getAddress(), BTItemEntry.Status.DISCONNECTED);
            if (!hasExist(device)) {
                mDialogAdapter.addItem(item);
            }
        }
    }

    private boolean hasExist(BluetoothDevice device){
        if(devicesMap.containsKey(device.getAddress())){
            return true;
        }
        devicesMap.put(device.getAddress(),device);
        return false;
    }

    public BluetoothDevice getBluetoothDevice(String address){
        return devicesMap.get(address);
    }

    public void updateBTState(String address, BTItemEntry.Status status){
        List<BTItemEntry> entrys = mDialogAdapter.getDataList();
        for(BTItemEntry entry:entrys){
            if(entry.getAddress().equals(address)){
                entry.setStatus(status);
                mDialogAdapter.update();
                return;
            }
        }
    }
}
