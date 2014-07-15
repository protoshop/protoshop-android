package com.ctrip.protoshop.widget;

import org.json.JSONObject;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.http.OnHttpListener;

public class HttpAsyncLayout extends FrameLayout implements OnHttpListener, OnClickListener {
	private OnHttpAsyncListner onHttpAsyncListner;

	private LinearLayout mProgressLayout;
	private LinearLayout mErrorLayout;

	public interface OnHttpAsyncListner {
		public void onSuccessListener(JSONObject response);

		public void onErrorListener(VolleyError error);

		public void onRefreshListener();
	}

	public HttpAsyncLayout(Context context) {
		super(context);
		initLayout();
	}

	public HttpAsyncLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout();
	}

	private void initLayout() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mErrorLayout = (LinearLayout) View.inflate(getContext(), R.layout.error_layout, null);
		mErrorLayout.findViewById(R.id.error_refresh_btn).setOnClickListener(this);
		mErrorLayout.setVisibility(View.GONE);
		addView(mErrorLayout, params);

		mProgressLayout = new LinearLayout(getContext());
		mProgressLayout.setGravity(Gravity.CENTER);
		ProgressBar progressBar = new ProgressBar(getContext());
		mProgressLayout.addView(progressBar);
		mProgressLayout.setVisibility(View.GONE);
		addView(mProgressLayout, params);
	}

	public void showProgress() {
		mProgressLayout.bringToFront();
		mProgressLayout.setVisibility(View.VISIBLE);
	}

	public void dismissProgress() {
		mProgressLayout.setVisibility(View.GONE);
	}

	@Override
	public void onHttpStart() {
		showProgress();
	}

	@Override
	public void onResponse(JSONObject response) {
		mProgressLayout.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
		if (onHttpAsyncListner != null) {
			onHttpAsyncListner.onSuccessListener(response);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		mProgressLayout.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.VISIBLE);
		mErrorLayout.bringToFront();
		if (error instanceof ParseError) {
			((TextView) mErrorLayout.findViewById(R.id.error_tip_view)).setText(R.string.no_content_text);
			mErrorLayout.findViewById(R.id.error_refresh_btn).setVisibility(View.GONE);
		} else {
			((TextView) mErrorLayout.findViewById(R.id.error_tip_view)).setText(R.string.error_text);
			mErrorLayout.findViewById(R.id.error_refresh_btn).setVisibility(View.VISIBLE);
		}
		if (onHttpAsyncListner != null) {
			onHttpAsyncListner.onErrorListener(error);
		}
	}

	public void showNoDataError() {
		mProgressLayout.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.VISIBLE);
		mErrorLayout.bringToFront();
		((TextView) mErrorLayout.findViewById(R.id.error_tip_view)).setText(R.string.no_content_text);
	}

	public OnHttpAsyncListner getOnHttpAsyncListner() {
		return onHttpAsyncListner;
	}

	public void setOnHttpAsyncListner(OnHttpAsyncListner onHttpAsyncListner) {
		this.onHttpAsyncListner = onHttpAsyncListner;
	}

	@Override
	public void onClick(View v) {
		if (onHttpAsyncListner != null) {
			onHttpAsyncListner.onRefreshListener();
		}
	}

}
