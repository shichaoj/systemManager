package com.bmob.im.demo.ui.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.ui.BaseActivity;

public class detailInfoActivity extends BaseActivity implements OnClickListener {

	TextView tv_set_name, tv_set_number, tv_set_gender , tv_set_id, tv_set_department;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_info);
		init();
		
		
		IntentFilter filter = new IntentFilter();		

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initDetailData();
	}
	
	
	
	private void initDetailData() {
		// TODO �Զ����ɵķ������
		User user = (User) getIntent().getSerializableExtra("user");
		
		tv_set_number.setText(user.getUsername());
		tv_set_name.setText(user.getName());
		tv_set_id.setText(user.getID());
		tv_set_department.setText(user.getDepartment());
		tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");
	}

	private void init() {
		// TODO �Զ����ɵķ������
		initTopBarForLeft("��ϸ��Ϣ");
		
		tv_set_number=(TextView) findViewById(R.id.tv_set_number);
		tv_set_name=(TextView) findViewById(R.id.tv_set_name);
		tv_set_id=(TextView) findViewById(R.id.tv_set_id);
		tv_set_gender=(TextView) findViewById(R.id.tv_set_gender);
		tv_set_department=(TextView) findViewById(R.id.tv_set_department);
		
	}

	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		
	}

}
