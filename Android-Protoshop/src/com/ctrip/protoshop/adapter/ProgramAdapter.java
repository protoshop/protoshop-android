package com.ctrip.protoshop.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ctrip.protoshop.ProtoshopApplication;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.model.ProgramModel;
import com.ctrip.protoshop.util.BitmapCache;

public class ProgramAdapter extends ProtoshopAdapter<ProgramModel> implements Filterable {
	private final Object mLock = new Object();

	private ImageLoader mImageLoader;
	private List<ProgramModel> mOriginalValues;
	private List<ProgramModel> mObjects;
	private ProgramFilter mFilter;

	public ProgramAdapter(Context context, List<ProgramModel> list) {
		super(context, list);
		mObjects = list;
		mImageLoader = new ImageLoader(ProtoshopApplication.getInstance().requestQueue, new BitmapCache());
	}

	@Override
	public View getView(int position, View convertView, Context context) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.program_item_layout, null);
			holder.iconView = (NetworkImageView) convertView.findViewById(R.id.program_icon_view);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.program_progressBar);
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

		model.state.showView(holder);

		holder.appNameView.setText(model.appName);
		holder.commentView.setText(model.comment);

		if (model.isPublic.endsWith("0")) {
			holder.isPublicView.setVisibility(View.VISIBLE);
		} else {
			holder.isPublicView.setVisibility(View.GONE);
		}

		return convertView;
	}

	public static class ViewHolder {
		public NetworkImageView iconView;
		public ProgressBar progressBar;
		public TextView appNameView;
		public TextView commentView;
		public TextView isPublicView;
		public TextView arrowView;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ProgramFilter();
		}
		return mFilter;
	}

	private class ProgramFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<ProgramModel>(mObjects);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				ArrayList<ProgramModel> list;
				synchronized (mLock) {
					list = new ArrayList<ProgramModel>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase(Locale.getDefault());

				ArrayList<ProgramModel> values;
				synchronized (mLock) {
					values = new ArrayList<ProgramModel>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<ProgramModel> newValues = new ArrayList<ProgramModel>();

				for (int i = 0; i < count; i++) {
					final ProgramModel value = values.get(i);
					final String valueText = value.appName.toString().toLowerCase(Locale.getDefault());

					// First match against the whole, non-splitted value
					if (valueText.startsWith(prefixString)) {
						newValues.add(value);
					} else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case valueText starts with space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(value);
								break;
							}
						}
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			List<ProgramModel> resultModels = (List<ProgramModel>) results.values;
			if (resultModels != null) {
				mObjects.clear();
				mObjects.addAll(resultModels);
				notifyDataSetChanged();
			}
		}
	}
}
