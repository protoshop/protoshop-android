package com.ctrip.protoshop.http;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

public class AppRequest extends StringRequest {
	private Map<String, String> mHeaders = new HashMap<String, String>(1);
	private Map<String, String> mParams;

	public AppRequest(String url, Listener<String> listener, ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	public AppRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	public AppRequest(String url, Map<String, String> params, Listener<String> listener, ErrorListener errorListener) {
		super(Method.POST, url, listener, errorListener);
		mParams = params;
	}

	public void setCookie(String cookie) {
		mHeaders.put("Cookie", cookie);
	}

	public void setParam(String key, String value) {
		if (mParams == null) {
			mParams = new HashMap<String, String>();
		}
		mParams.put(key, value);
	}

	public void setParams(Map<String, String> params) {
		mParams = params;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}
}
