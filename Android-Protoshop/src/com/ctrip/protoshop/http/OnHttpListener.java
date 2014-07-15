package com.ctrip.protoshop.http;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public interface OnHttpListener extends Listener<String>, ErrorListener {
    public void onHttpStart();
}
