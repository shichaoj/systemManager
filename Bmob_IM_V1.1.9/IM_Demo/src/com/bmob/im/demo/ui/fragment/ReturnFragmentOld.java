package com.bmob.im.demo.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.bmob.im.db.BmobDB;

import com.bmob.im.demo.manager.R;
import com.bmob.im.demo.adapter.MessageRecentAdapter;
import com.bmob.im.demo.adapter.ReturnAdapter;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.view.ClearEditText;

public class ReturnFragmentOld extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{

	ClearEditText mClearEditText;
	
	ListView listview;
	
	ReturnAdapter adapter;
	
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
	
	
	private void initView() {
		// TODO �Զ����ɵķ������
		initTopBarForOnlyTitle("�黹");
		listview = (ListView)findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		//adapter = new ReturnAdapter(getActivity(), R.layout.item_return, BmobDB.create(getActivity()).queryRecents());
		listview.setAdapter(adapter);
		
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

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO �Զ����ɵķ������
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO �Զ����ɵķ������
		
	}

}
