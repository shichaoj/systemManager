package com.bmob.im.demo.ui.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.LoginActivity;
import com.bmob.im.demo.ui.UpdateInfoActivity;
import com.bmob.im.demo.util.SharePreferenceUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility", "InflateParams" })
public class MyInfoFragment extends FragmentBase implements OnClickListener{
	TextView tv_set_name, tv_set_number, tv_set_gender , tv_set_id, tv_set_department;
	Button btn_logout;
	LinearLayout layout_all;

	Button btn_chat, btn_back, btn_add_friend;
	RelativeLayout layout_head, layout_nick, layout_gender, layout_black_tips;

	String username = "";
	User user;
	
	SharePreferenceUtil mSharedUtil;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedUtil = mApplication.getSpUtil();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragement_my_info, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		//initData();
	}

	private void initView() {
		// TODO 自动生成的方法存根
		initTopBarForOnlyTitle("个人信息（管理员）");
		tv_set_number=(TextView) findViewById(R.id.tv_set_number);
		tv_set_name=(TextView) findViewById(R.id.tv_set_name);
		tv_set_id=(TextView) findViewById(R.id.tv_set_id);
		tv_set_gender=(TextView) findViewById(R.id.tv_set_gender);
		tv_set_department=(TextView) findViewById(R.id.tv_set_department);
		
		btn_logout = (Button) findViewById(R.id.btn_logout);
		
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
		
		btn_logout.setOnClickListener(this);
		layout_gender.setOnClickListener(this);
		
	}
	
	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("hight = "+user.getHight()+",sex= "+user.getSex());
		initOtherData(user.getUsername());
	}
	
	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					updateUser(user);
				} else {
					ShowLog("onSuccess 查无此人");
				}
			}
		});
	}
	
	private void updateUser(User user) {
		// 更改
		tv_set_number.setText(user.getUsername());	
		tv_set_name.setText(user.getName());
		tv_set_id.setText(user.getID());
		tv_set_department.setText(user.getDepartment());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initMeData();
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) { 
		case R.id.layout_gender:// 性别
			showSexChooseDialog();
			break;
		case R.id.btn_logout:
			CustomApplcation.getInstance().logout();
			getActivity().finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		}
	}
	
	String[] sexs = new String[]{ "男", "女" };
	private void showSexChooseDialog() {
		new AlertDialog.Builder(getActivity())
		.setTitle("单选框")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(sexs, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						BmobLog.i("点击的是"+sexs[which]);
						updateInfo(which);
						dialog.dismiss();
					}
				})
		.setNegativeButton("取消", null)
		.show();
	}
	
	private void updateInfo(int which) {
		final User u = new User();
		if(which==0){
			u.setSex(true);
		}else{
			u.setSex(false);
		}
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("修改成功");
				tv_set_gender.setText(u.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(getActivity(),listener);
	}

}
