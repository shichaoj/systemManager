package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.adapter.AddFriendAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.view.xlist.XListView;
import com.bmob.im.demo.view.xlist.XListView.IXListViewListener;

/** 
  * @ClassName: AddFriendActivity
  * @Description: TODO
  * @author smile
  * @date 2014-6-5 下午5:26:41
  */
public class AddFriendActivity extends ActivityBase implements OnClickListener,IXListViewListener,OnItemClickListener{
	
	EditText et_find_name , et_machine_id;
	Button btn_search , btn_submit;
	
	List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	XListView mListView;
	AddFriendAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_borrow);
		initView();
	}
	
	private void initView(){
		initTopBarForLeft("添加借用信息");
		et_find_name = (EditText)findViewById(R.id.et_find_name);
		et_machine_id = (EditText)findViewById(R.id.et_machine_id);
		btn_search = (Button)findViewById(R.id.btn_search);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		btn_search.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		//initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_search);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 不允许下拉
		mListView.setPullRefreshEnable(false);
		// 设置监听器
		mListView.setXListViewListener(this);
		//
		mListView.pullRefreshing();
		
		adapter = new AddFriendAdapter(this, users);
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(this);
	}
	
	int curPage = 0;
	ProgressDialog progress ;
	private void initSearchList(final boolean isUpdate){
		if(!isUpdate){
			progress = new ProgressDialog(AddFriendActivity.this);
			progress.setMessage("正在搜索...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		userManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i("查询错误:"+arg1);
				if(users!=null){
					users.clear();
				}
				ShowToast("用户不存在");
				mListView.setPullLoadEnable(false);
				refreshPull();
				//这样能保证每次查询都是从头开始
				curPage = 0;
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				if (CollectionUtils.isNotNull(arg0)) {
					if(isUpdate){
						users.clear();
					}
					adapter.addAll(arg0);
					if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
						mListView.setPullLoadEnable(false);
						ShowToast("用户搜索完成!");
					}else{
						mListView.setPullLoadEnable(true);
					}
				}else{
					BmobLog.i("查询成功:无返回值");
					if(users!=null){
						users.clear();
					}
					ShowToast("用户不存在");
				}
				if(!isUpdate){
					progress.dismiss();
				}else{
					refreshPull();
				}
				//这样能保证每次查询都是从头开始
				curPage = 0;
			}
		});
		
	}
	
	/** 查询更多
	  * @Title: queryMoreNearList
	  * @Description: TODO
	  * @param @param page 
	  * @return void
	  * @throws
	  */
	private void queryMoreSearchList(int page){
		userManager.queryUserByPage(true, page, searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				if (CollectionUtils.isNotNull(arg0)) {
					adapter.addAll(arg0);
				}
				refreshLoad();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("搜索更多用户出错:"+arg1);
				mListView.setPullLoadEnable(false);
				refreshLoad();
			}

		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobChatUser user = (BmobChatUser) adapter.getItem(position-1);
		Intent intent =new Intent(this,SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);		
	}
	
	String searchName , machineID ="";
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_search://搜索
			testUsername(false , null);
			break;
		case R.id.btn_submit:			
			machineID = et_machine_id.getText().toString();
			if(machineID!=null && !machineID.equals("")){
				testUsername(true , machineID);
			}else{
				ShowToast("请输入样机编号");
			}
			break;
		default:
			break;
		}
	}
	
	private void testUsername(final Boolean isSubmit , final String machineID){
		users.clear();
		searchName = et_find_name.getText().toString();
		if(searchName!=null && !searchName.equals("")){
			queryUsername(searchName , isSubmit , machineID);
		}else{
			ShowToast("请输入用户名");
		}
	}
	
	private void queryUsername(final String username , final Boolean isSubmit , final String machineID){
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", username);
		query.findObjects(this, new FindListener<User>() {
			  
			@Override
			public void onError(int arg0, String arg1) {
				// TODO 自动生成的方法存根
				ShowToast("查询失败");
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO 自动生成的方法存根
		        //集合为空	
				if(arg0.size()==0){
					ShowToast("该用户名不存在");
				}else{
					User user = arg0.get(0);
					String objectID = user.getObjectId();
					if(user.getMachineID().equals("0")){
						if(isSubmit){
							Long currentTime = System.currentTimeMillis() / 1000L ;
							String borrowTime = currentTime + "";
							cloudUpdate(borrowTime, machineID , objectID);							
						}else{
							ShowToast("该用户无借用信息");
						}
					}else{
						ShowToast("该用户已有借用信息");
					}
				}
			}
		});
	}
	
	private void cloudUpdate(final String borrowTime , final String machineID , final String objectID){
		String cloudCodeName = "updateBorrow";
		JSONObject params = new JSONObject();
		try {
			params.put("borrowTime", borrowTime);
			params.put("machineID", machineID);
			params.put("objectID", objectID);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			ShowToast("数据处理出错");
			e.printStackTrace();
		}
		
		//创建云端逻辑对象
		AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
		//异步调用云端逻辑
		cloudCode.callEndpoint(this, cloudCodeName, params, new CloudCodeListener() {

		    //执行成功时调用，返回result对象
		    @Override
		    public void onSuccess(Object result) {
		        Log.i("bmob", "result = "+result.toString());
		        ShowToast("借用数据提交成功！");
		    }

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO 自动生成的方法存根
				ShowToast("上传数据失败！");
				Log.i("失败原因",arg1);
			}
		});
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		userManager.querySearchTotalCount(searchName, new CountListener() {
			
			@Override
			public void onSuccess(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 >users.size()){
					curPage++;
					queryMoreSearchList(curPage);
				}else{
					ShowToast("数据加载完成");
					mListView.setPullLoadEnable(false);
					refreshLoad();
				}
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("查询附近的人总数失败"+arg1);
				refreshLoad();
			}
		});
	}
	
	private void refreshLoad(){
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}
	
	private void refreshPull(){
		if (mListView.getPullRefreshing()) {
			mListView.stopRefresh();
		}
	}
	

}
