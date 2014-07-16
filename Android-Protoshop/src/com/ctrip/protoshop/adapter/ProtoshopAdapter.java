package com.ctrip.protoshop.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ProtoshopAdapter<T> extends BaseAdapter {
	private Context mContext;
	private List<T> mList;

	public ProtoshopAdapter(Context context, List<T> list) {
		mContext = context;
		mList = list;
	}

	public void updateList(List<T> list) {
		mList = list;
	}

	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mList != null ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, mContext);
	}

	public abstract View getView(int position, View convertView, Context context);

}
