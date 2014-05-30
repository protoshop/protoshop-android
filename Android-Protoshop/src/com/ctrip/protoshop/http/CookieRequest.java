package com.ctrip.protoshop.http;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class CookieRequest extends JsonObjectRequest {
    private Map<String, String> mHeaders=new HashMap<String, String>(1);

    public CookieRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
                                   ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    public CookieRequest(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener,
                                   ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }
    
    public void setCookie(String cookie){
        mHeaders.put("Cookie", cookie);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

}
