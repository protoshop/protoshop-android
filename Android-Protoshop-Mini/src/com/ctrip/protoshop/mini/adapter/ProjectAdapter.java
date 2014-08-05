package com.ctrip.protoshop.mini.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctrip.protoshop.mini.R;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.Util;

public class ProjectAdapter extends MiniAdapter {

	public ProjectAdapter(Context context, List<?> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, final Context context) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.project_item_layout, null);
			holder.thumbView = (ImageView) convertView.findViewById(R.id.project_icon_view);
			holder.nameView = (TextView) convertView.findViewById(R.id.project_name_view);
			holder.pageNumView = (TextView) convertView.findViewById(R.id.project_page_num_view);
			holder.deleteView = (TextView) convertView.findViewById(R.id.project_item_confirm_view);
			holder.deleteView.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.deleteView.setGravity(Gravity.CENTER_VERTICAL);

		ProjectModel model = (ProjectModel) getItem(position);

		holder.nameView.setText(model.appName);
		int pageNum = model.scenes == null ? 0 : model.scenes.size();
		holder.pageNumView.setText(pageNum + context.getString(R.string.page_text));
		if (pageNum != 0) {
			String bitmapPath = Util.getLocalProFile(context, model.appID).getAbsolutePath() + File.separator + model.scenes.get(0).background;
			Bitmap bitmap = Util.getBitmap(bitmapPath);
			holder.thumbView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, context.getResources().getDimensionPixelSize(R.dimen._60), context.getResources()
					.getDimensionPixelSize(R.dimen._60)));
		} else {
			holder.thumbView.setImageResource(R.drawable.ic_launcher);
		}

		holder.deleteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("delete item");
				intent.putExtra("position", position);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				holder.deleteView.setVisibility(View.GONE);
			}
		});

		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				holder.deleteView.setVisibility(View.VISIBLE);
				holder.deleteView.setGravity(Gravity.CENTER_VERTICAL);
				return true;
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (holder.deleteView.getVisibility() == View.GONE) {
					Intent intent = new Intent("on item click");
					intent.putExtra("position", position);
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				} else {
					holder.deleteView.setVisibility(View.GONE);
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageView thumbView;
		TextView nameView;
		TextView pageNumView;
		TextView deleteView;
	}
}
