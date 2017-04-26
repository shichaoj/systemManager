package com.bmob.im.demo.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.bmob.im.demo.util.FaceTextUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/** �Ự������
  * @ClassName: MessageRecentAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-7 ����2:34:10
  */
public class MessageRecentAdapter extends ArrayAdapter<User> implements Filterable{
 

	
	private LayoutInflater inflater;
	private List<User> mData;
	private Context mContext;
	private User user;
	
	public MessageRecentAdapter(Context context, int textViewResourceId, List<User> objects) {
		super(context, textViewResourceId, objects);
		inflater = LayoutInflater.from(context);
		this.mContext = context;
		mData = objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		

		// TODO Auto-generated method stub
		final User user = mData.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_borrow, parent, false);
		}
		ImageView iv_recent_avatar = ViewHolder.get(convertView, R.id.iv_recent_avatar);
		TextView tv_recent_name = ViewHolder.get(convertView, R.id.tv_recent_name);
		TextView tv_recent_msg = ViewHolder.get(convertView, R.id.tv_recent_msg);
		TextView tv_recent_time = ViewHolder.get(convertView, R.id.tv_recent_time);
		TextView tv_recent_unread = ViewHolder.get(convertView, R.id.tv_recent_unread);
		
		//�������
		iv_recent_avatar.setImageResource(R.drawable.machine);
	
		
		tv_recent_name.setText(user.getName());
		tv_recent_msg.setText("��ţ�"+user.getMachineID());
		
		long borrowTime = Long.parseLong(user.getBorrowTime());
		
		tv_recent_time.setText("���ڣ�"+TimeUtil.getChatTime(borrowTime));
		//��ʾ����
//		if(item.getType()==BmobConfig.TYPE_TEXT){
//			SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, item.getMessage());
//			tv_recent_msg.setText("��ţ�"+spannableString);
//		}else if(item.getType()==BmobConfig.TYPE_IMAGE){
//			tv_recent_msg.setText("[ͼƬ]");
//		}else if(item.getType()==BmobConfig.TYPE_LOCATION){
//			String all =item.getMessage();
//			if(all!=null &&!all.equals("")){//λ�����͵���Ϣ��װ��ʽ������λ��&ά��&����
//				String address = all.split("&")[0];
//				tv_recent_msg.setText("[λ��]"+address);
//			}
//		}else if(item.getType()==BmobConfig.TYPE_VOICE){
//			tv_recent_msg.setText("[����]");
//		}
//		
		tv_recent_unread.setVisibility(View.GONE);

		return convertView;
	}

}
