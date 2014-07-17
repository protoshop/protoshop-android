package com.ctrip.protoshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.android.volley.VolleyError;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.http.OnHttpListener;

public class HttpAsyncLayout extends FrameLayout implements OnHttpListener, OnClickListener {
	private OnHttpAsyncListner onHttpAsyncListner;

	private View mProgressView;
	private View mErrorView;
	private View mNoDataView;

	private boolean isOnlyProgressbar = false;

	public interface OnHttpAsyncListner {
		public void onSuccessListener(String response);

		public void onErrorListener(VolleyError error);

		public void onRefreshListener();
	}

	public HttpAsyncLayout(Context context) {
		super(context);
		initLayout(R.layout.default_http_error_layout, R.layout.default_http_progress_layout, R.layout.default_http_nodata_layout);
	}

	public HttpAsyncLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HttpAsyncLayout);
		int errorRes = a.getResourceId(R.styleable.HttpAsyncLayout_error_layout, 0);
		if (errorRes == 0) {
			errorRes = R.layout.default_http_error_layout;
		}
		int progressRes = a.getResourceId(R.styleable.HttpAsyncLayout_progress_layout, 0);
		if (progressRes == 0) {
			progressRes = R.layout.default_http_progress_layout;
		}
		int nodataRes = a.getResourceId(R.styleable.HttpAsyncLayout_no_data_layout, 0);
		if (nodataRes == 0) {
			nodataRes = R.layout.default_http_nodata_layout;
		}

		isOnlyProgressbar = a.getBoolean(R.styleable.HttpAsyncLayout_only_progressbar, false);

		a.recycle();
		initLayout(errorRes, progressRes, nodataRes);
	}

	private void initLayout(int errorRes, int progressRes, int nodataRes) {
		mProgressView = View.inflate(getContext(), progressRes, null);
		addView(mProgressView);
		mProgressView.setVisibility(View.GONE);
		mErrorView = View.inflate(getContext(), errorRes, null);
		addView(mErrorView);
		mErrorView.setVisibility(View.GONE);
		mNoDataView = View.inflate(getContext(), nodataRes, null);
		addView(mNoDataView);
		mNoDataView.setVisibility(View.GONE);
	}

	public void showProgress() {
		mProgressView.bringToFront();
		mProgressView.setVisibility(View.VISIBLE);
		mErrorView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
	}

	public void dismissProgress() {
		mProgressView.setVisibility(View.GONE);
	}

	@Override
	public void onHttpStart() {
		showProgress();
	}

	@Override
	public void onResponse(String response) {
		mProgressView.setVisibility(View.GONE);
		mErrorView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		if (onHttpAsyncListner != null) {
			onHttpAsyncListner.onSuccessListener(response);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		mProgressView.setVisibility(View.GONE);
		mNoDataView.setVisibility(View.GONE);
		if (!isOnlyProgressbar) {
			mErrorView.setVisibility(View.VISIBLE);
			mErrorView.bringToFront();
		}
		if (onHttpAsyncListner != null) {
			onHttpAsyncListner.onErrorListener(error);
		}
	}

	public void showNoDataError() {
		mProgressView.setVisibility(View.GONE);
		mErrorView.setVisibility(View.GONE);
		if (!isOnlyProgressbar) {
			mNoDataView.setVisibility(View.VISIBLE);
			mNoDataView.bringToFront();
		}
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
