package com.bmob.im.demo.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;

import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.ui.fragment.ReturnFragment;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/** 好友列表
  * @ClassName: UserFriendAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-12 下午3:03:40
  */
@SuppressLint("DefaultLocale")
public class ReturnAdapter extends BaseAdapter implements SectionIndexer {
	private Context ct;
	private List<User> data;

	public ReturnAdapter(Context ct, List<User> datas) {
		this.ct = ct;
		this.data = datas;
	}

	/** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<User> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(User user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(
					R.layout.item_return, null);
			viewHolder = new ViewHolder();
			viewHolder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_recent_name);
			viewHolder.avatar = (ImageView) convertView
					.findViewById(R.id.iv_recent_avatar);
			viewHolder.msg = (TextView) convertView
					.findViewById(R.id.tv_recent_msg);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_recent_time);
			viewHolder.returned = (Button) convertView
					.findViewById(R.id.tv_recent_returned);
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		User friend = data.get(position);
		final String name = friend.getUsername();
		final String avatar = friend.getAvatar();
		final Boolean isRenew =  friend.getIsRenew();
		final String machineID = friend.getMachineID();
		final Long borrowTime = Long.parseLong(friend.getBorrowTime());
		final String objectID = friend.getObjectId();
		

		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, viewHolder.avatar, ImageLoadOptions.getOptions());
		} else {
			viewHolder.avatar.setImageDrawable(ct.getResources().getDrawable(R.drawable.machine));
		}
		viewHolder.name.setText(name);
		viewHolder.msg.setText("编号："+machineID);
		viewHolder.time.setText("日期："+TimeUtil.getReturnTime(borrowTime,isRenew));

		viewHolder.returned.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Log.i("按钮点击","第"+position+"个按钮");
            	returnDialog(objectID);
            }
        });
		
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.alpha.setVisibility(View.VISIBLE);
			viewHolder.alpha.setText(friend.getSortLetters());
		} else {
			viewHolder.alpha.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView alpha,msg,time;// 首字母提示
		ImageView avatar;
		TextView name;
		Button returned;
		
	}
	
	private void returnDialog(final String objectID){
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        //    设置Title的图标
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //    设置Title的内容
        builder.setTitle("警告框");
        //    设置Content来显示一个信息
        builder.setMessage("该条借用信息将删除，您确认该用户已归还吗？");
        //    设置一个PositiveButton
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Toast.makeText(ct, "该用户借用信息已删除！", Toast.LENGTH_SHORT).show();
            	cloudUpdate(objectID);
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
              
            }
        });
        
        //    显示出该对话框
        builder.show();
	}
	
	private void cloudUpdate(final String objectID){
		String cloudCodeName = "deleteBorrow";
		JSONObject params = new JSONObject();
		try {
			params.put("objectID", objectID);
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			 Toast.makeText(ct, "信息删除异常！", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		//创建云端逻辑对象
		AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
		//异步调用云端逻辑
		cloudCode.callEndpoint(ct, cloudCodeName, params, new CloudCodeListener() {

		    //执行成功时调用，返回result对象
		    @Override
		    public void onSuccess(Object result) {
		        Log.i("成功删除", "result = "+result.toString());
		        Toast.makeText(ct, "该用户借用信息已删除！", Toast.LENGTH_SHORT).show();
		    }

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO 自动生成的方法存根
				 Toast.makeText(ct, "信息删除出错！", Toast.LENGTH_SHORT).show();
				Log.i("失败原因",arg1);
			}
		});
	}
	

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return data.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = data.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section){
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}