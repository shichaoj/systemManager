package com.bmob.im.demo.ui.fragment;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.AddFriendActivity;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.view.ClearEditText;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.dialog.DialogTips;

/** 最近会话
  * @ClassName: ConversationFragment
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 下午1:01:37
  */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{

	ClearEditText mClearEditText;
	
	ListView listview;
	
	MessageRecentAdapter adapter;
	
	private List<User> userList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
		
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereNotEqualTo("username", "shichaor");
		query.addWhereNotEqualTo("machineID", "0");
		query.findObjects(getActivity(), new FindListener<User>() {
		  
			@Override
			public void onError(int arg0, String arg1) {
				// TODO 自动生成的方法存根
				Log.i("查询username失败",arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO 自动生成的方法存根
				userList=arg0;
				passData(userList);
				//Log.i("查询username成功",user.getName());
				//BmobLog.i(user.getName());
			}
		});
		
		//initTopBarForOnlyTitle("借用");
		initTopBarForRight("借用", R.drawable.base_action_bar_add_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						startAnimActivity(AddFriendActivity.class);
					}
				});
	
		
		mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}
	
	//数据传输
	private void passData(List<User> userList){
		listview = (ListView)findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_borrow, userList);
		listview.setAdapter(adapter);
	}
	
	
	
	/** 删除会话
	  * deleteRecent
	  * @param @param recent 
	  * @return void
	  * @throws
	  */
	private void deleteRecent(BmobRecent recent){
		//adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		//BmobRecent recent = adapter.getItem(position);
		//showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getUserName(),"删除会话", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		User user = adapter.getItem(position);
	
		
//		BmobChatUser user = new BmobChatUser();
//		user.setAvatar(recent.getAvatar());
//		user.setNick(recent.getNick());
//		user.setUsername(recent.getUserName());
//		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), detailInfoActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {    
					//listview.setAdapter(adapter);
					initView();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
	
}
