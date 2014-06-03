package com.ctrip.protoshop.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ctrip.protoshop.ProtoshopApplication;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.model.ProgramModel;
import com.ctrip.protoshop.util.BitmapCache;

public class ProgramAdapter extends ProtoshopAdapter {

	private ImageLoader mImageLoader;

	public ProgramAdapter(Context context, List<?> list) {
		super(context, list);
		mImageLoader = new ImageLoader(ProtoshopApplication.getInstance().requestQueue, new BitmapCache());
	}

	@Override
	public View getView(int position, View convertView, Context context) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.program_item_layout, null);
			holder.iconView = (NetworkImageView) convertView.findViewById(R.id.program_icon_view);
			holder.appNameView = (TextView) convertView.findViewById(R.id.program_name_view);
			holder.commentView = (TextView) convertView.findViewById(R.id.program_comment_view);
			holder.isPublicView = (TextView) convertView.findViewById(R.id.program_secret_view);
			holder.arrowView = (TextView) convertView.findViewById(R.id.program_arrow_view);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ProgramModel model = (ProgramModel) getItem(position);

		holder.iconView.setDefaultImageResId(R.drawable.ic_launcher);
		holder.iconView.setErrorImageResId(R.drawable.ic_launcher);
		holder.iconView.setImageUrl(model.icon, mImageLoader);

		holder.appNameView.setText(model.appName);
		holder.commentView.setText(model.comment);

		if (model.isPublic.endsWith("0")) {
			holder.isPublicView.setVisibility(View.VISIBLE);
		} else {
			holder.isPublicView.setVisibility(View.GONE);
		}

		if (model.isLoadZip) {
			holder.arrowView.setVisibility(View.GONE);
		} else {
			holder.arrowView.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		NetworkImageView iconView;
		TextView appNameView;
		TextView commentView;
		TextView isPublicView;
		TextView arrowView;
	}
}
