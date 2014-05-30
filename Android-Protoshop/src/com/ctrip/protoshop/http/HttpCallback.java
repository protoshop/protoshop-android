package com.ctrip.protoshop.http;

import org.json.JSONObject;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public interface HttpCallback extends Listener<JSONObject>, ErrorListener {
    public void onHttpStart();
}
