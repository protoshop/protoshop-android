package com.ctrip.protoshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.appcompat.R;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MorePopupAdapter extends BaseAdapter {
	private Menu mMenu;
	private Context mContext;
	private int mContentWidth;

	public MorePopupAdapter(Context context, Menu menu) {
		mMenu = menu;
		mContext = context;
		mContentWidth = Math.min(context.getResources().getDisplayMetrics().widthPixels / 2, context.getResources().getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
	}

	public int getWidth() {
		return Math.min(mContentWidth, measureContentWidth());
	}

	public int getCount() {
		return mMenu.size();
	}

	public Object getItem(int position) {
		return mMenu.getItem(position);
	}

	public long getItemId(int position) {
		return mMenu.getItem(position).getItemId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MenuItem item = mMenu.getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.abc_activity_chooser_view_list_item, parent, false);
		}

		ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
		iconView.setImageDrawable(item.getIcon());

		TextView titleView = (TextView) convertView.findViewById(R.id.title);
		titleView.setText(item.getTitle());
		titleView.setTextColor(Color.WHITE);

		return convertView;

	}

	public int measureContentWidth() {

		int contentWidth = 0;
		View itemView = null;

		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
		int count = getCount();

		for (int i = 0; i < count; i++) {
			itemView = getView(i, itemView, null);
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
			contentWidth = Math.max(contentWidth, itemView.getMeasuredWidth());
		}

		return contentWidth;
	}

}
