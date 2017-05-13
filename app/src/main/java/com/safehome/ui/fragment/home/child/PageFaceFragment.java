package com.safehome.ui.fragment.home.child;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.blankj.utilcode.utils.ToastUtils;
import com.safehome.R;
import com.safehome.ui.activity.main.HomeActivity;

/**
 * @auther gbh
 * Created on 2017/5/7.
 */

public class PageFaceFragment extends BaseHomeFragment implements OnClickListener{
	private Button openOrcloseBtn;
	private boolean openFlag;
	
	public PageFaceFragment(){
		openFlag = false;
	}

	@Override
	protected int getContentViewResId(){
		return R.layout.layout_fragment_face;
	}

	@Override
	protected void initWidgets(){
		((Button)rootView.findViewById(R.id.face_delete)).setOnClickListener(this);
		((Button)rootView.findViewById(R.id.face_deleteall)).setOnClickListener(this);
		((Button)rootView.findViewById(R.id.face_identification)).setOnClickListener(this);
		openOrcloseBtn = (Button)rootView.findViewById(R.id.face_open);
		openOrcloseBtn.setOnClickListener(this);
		((Button)rootView.findViewById(R.id.face_register)).setOnClickListener(this);
	}

	@Override
	protected void onFragmentVisibleChange(boolean isVisible){
		super.onFragmentVisibleChange(isVisible);
		if (isVisible){
			CURRENT_FRAGMENT = 1;
	    } 
		else{
	    }
	}
	@Override
	public void onClick(View v) {
		switch(HomeActivity.commandStyle){
			case 0:
				sendCmdByBluetooth(v);
				break;
			case 1:
				sendCmdByGPRS(v);
				break;
			default:
				ToastUtils.showShortToast("请选择命令发送方式");
		}
	}

	private void sendCmdByBluetooth(View v){
		String cmd = null;
		String data = "0000";
		switch (v.getId()) {
			case R.id.face_open:
				cmd = "07";
				break;
			case R.id.face_register:
				cmd = "08";
				break;
			case R.id.face_identification:
				cmd = "09";
				break;
			case R.id.face_delete:
				cmd = "A0";
				break;
			case R.id.face_deleteall:
				//if(!openFlag){//尚未开启
					cmd = "A1";
				//}
				//else{
				//	cmd = "06";
				//}
				break;
		}
		submitTask(cmd,data);
	}

	private void sendCmdByGPRS(View v){
		String cmd = null;
		switch (v.getId()) {
			case R.id.face_open:
				cmd = "OpenXF";
				break;
			case R.id.face_register:
				cmd = "EnrollXF";
				break;
			case R.id.face_delete:
				cmd = "DeleteXFOne";
				break;
			case R.id.face_deleteall:
				cmd = "DeleteXFAll";
				break;
//			case R.id.fa:
//				if(!openFlag){//尚未开启
//					openFlag = true;
//					cmd = "OpenFinger";
//					//openOrcloseBtn.setBackgroundDrawable(closeStateListDrawable);
//				}
//				else{
//					openFlag = false;
//					cmd = "CloseFinger";
//					//openOrcloseBtn.setBackgroundDrawable(openStateListDrawable);
//				}
//				break;
		}
		sendDataByGPRS(cmd);
	}
}
