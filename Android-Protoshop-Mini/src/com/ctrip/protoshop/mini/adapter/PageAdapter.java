package com.ctrip.protoshop.mini.adapter;

import java.io.File;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctrip.protoshop.mini.R;
import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.Util;

public class PageAdapter extends BaseAdapter {
	private ProjectModel mLocalModel;
	private Context mContext;

	public PageAdapter(Context context, ProjectModel model) {
		mLocalModel = model;
		mContext = context;

	}

	@Override
	public int getCount() {
		return mLocalModel.scenes != null ? mLocalModel.scenes.size() : 0;
	}

	@Override
	public Object getItem(int position) {

		return mLocalModel.scenes != null ? mLocalModel.scenes.get(position) : null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.page_item_layout, null);
			holder.deleteView = (ImageView) convertView.findViewById(R.id.page_delete_view);
			holder.thumbnailView = (ImageView) convertView.findViewById(R.id.page_thumb_view);
			holder.nameView = (TextView) convertView.findViewById(R.id.page_name_view);
			holder.linkView =  convertView.findViewById(R.id.selected_contaner);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PageModel model = mLocalModel.scenes.get(position);
		Bitmap bitmap = Util.getBitmap(Util.getLocalProFile(mContext, mLocalModel.appID).getAbsolutePath() + File.separator + model.background);

		if (model.isEditModel) {
			holder.deleteView.setVisibility(View.VISIBLE);
			holder.deleteView.setOnClickListener(new DeleteListener(position));
		} else {
			holder.deleteView.setVisibility(View.GONE);
		}

		if (model.isLinkPage) {
			holder.linkView.setVisibility(View.VISIBLE);
		} else {
			holder.linkView.setVisibility(View.GONE);
		}

		holder.thumbnailView.setImageBitmap(bitmap);
		holder.nameView.setText(model.name);
		return convertView;
	}

	static class ViewHolder {
		ImageView deleteView;
		ImageView thumbnailView;
		TextView nameView;
		View linkView;
	}

	class DeleteListener implements OnClickListener {
		int position;

		public DeleteListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			mLocalModel.scenes.remove(position);
			notifyDataSetChanged();
		}

	}
}
