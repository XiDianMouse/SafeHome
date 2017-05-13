package com.safehome.ui.fragment.home.child;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.dialog.BTDialog;
import com.safehome.ui.activity.main.HomeActivity;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class PageConnectFragment extends BaseHomeFragment implements OnClickListener{
	private BTDialog btDialog;
	private HomeActivity activity;

	public PageConnectFragment(){
	}

	@Override
	protected int getContentViewResId(){
		return R.layout.layout_fragment_connect;
	}

	@Override
	protected void initWidgets(){
		((Button)rootView.findViewById(R.id.select_bluetoothConnect)).setOnClickListener(this);
		((Button)rootView.findViewById(R.id.select_gprsConnect)).setOnClickListener(this);
		btDialog=new BTDialog(getActivity());
	}

	@Override
	protected void onFragmentVisibleChange(boolean isVisible){
		super.onFragmentVisibleChange(isVisible);
		if (isVisible){//do things when fragment is visible            
			CURRENT_FRAGMENT = 2;
	    } 
		else{
	    }
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.select_bluetoothConnect:
				HomeActivity.commandStyle = 0;
				btDialog.showDialog();
			break;
			case R.id.select_gprsConnect:
				HomeActivity.commandStyle = 1;
				ToastUtils.showShortToast("通过GPRS下发");
				break;
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		btDialog.destory();
		btDialog=null;
	}
}
