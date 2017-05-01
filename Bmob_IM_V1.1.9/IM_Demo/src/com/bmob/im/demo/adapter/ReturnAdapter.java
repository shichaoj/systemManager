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

/** �����б�
  * @ClassName: UserFriendAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-12 ����3:03:40
  */
@SuppressLint("DefaultLocale")
public class ReturnAdapter extends BaseAdapter implements SectionIndexer {
	private Context ct;
	private List<User> data;

	public ReturnAdapter(Context ct, List<User> datas) {
		this.ct = ct;
		this.data = datas;
	}

	/** ��ListView���ݷ����仯ʱ,���ô˷���������ListView
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
		viewHolder.msg.setText("��ţ�"+machineID);
		viewHolder.time.setText("���ڣ�"+TimeUtil.getReturnTime(borrowTime,isRenew));

		viewHolder.returned.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Log.i("��ť���","��"+position+"����ť");
            	returnDialog(objectID);
            }
        });
		
		// ����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);
		// �����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
		if (position == getPositionForSection(section)) {
			viewHolder.alpha.setVisibility(View.VISIBLE);
			viewHolder.alpha.setText(friend.getSortLetters());
		} else {
			viewHolder.alpha.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView alpha,msg,time;// ����ĸ��ʾ
		ImageView avatar;
		TextView name;
		Button returned;
		
	}
	
	private void returnDialog(final String objectID){
        //    ͨ��AlertDialog.Builder�������ʵ�������ǵ�һ��AlertDialog�Ķ���
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        //    ����Title��ͼ��
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //    ����Title������
        builder.setTitle("�����");
        //    ����Content����ʾһ����Ϣ
        builder.setMessage("����������Ϣ��ɾ������ȷ�ϸ��û��ѹ黹��");
        //    ����һ��PositiveButton
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Toast.makeText(ct, "���û�������Ϣ��ɾ����", Toast.LENGTH_SHORT).show();
            	cloudUpdate(objectID);
            }
        });
        //    ����һ��NegativeButton
        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
              
            }
        });
        
        //    ��ʾ���öԻ���
        builder.show();
	}
	
	private void cloudUpdate(final String objectID){
		String cloudCodeName = "deleteBorrow";
		JSONObject params = new JSONObject();
		try {
			params.put("objectID", objectID);
		} catch (JSONException e) {
			// TODO �Զ����ɵ� catch ��
			 Toast.makeText(ct, "��Ϣɾ���쳣��", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		//�����ƶ��߼�����
		AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
		//�첽�����ƶ��߼�
		cloudCode.callEndpoint(ct, cloudCodeName, params, new CloudCodeListener() {

		    //ִ�гɹ�ʱ���ã�����result����
		    @Override
		    public void onSuccess(Object result) {
		        Log.i("�ɹ�ɾ��", "result = "+result.toString());
		        Toast.makeText(ct, "���û�������Ϣ��ɾ����", Toast.LENGTH_SHORT).show();
		    }

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO �Զ����ɵķ������
				 Toast.makeText(ct, "��Ϣɾ������", Toast.LENGTH_SHORT).show();
				Log.i("ʧ��ԭ��",arg1);
			}
		});
	}
	

	/**
	 * ����ListView�ĵ�ǰλ�û�ȡ���������ĸ��Char asciiֵ
	 */
	public int getSectionForPosition(int position) {
		return data.get(position).getSortLetters().charAt(0);
	}

	/**
	 * ���ݷ��������ĸ��Char asciiֵ��ȡ���һ�γ��ָ�����ĸ��λ��
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