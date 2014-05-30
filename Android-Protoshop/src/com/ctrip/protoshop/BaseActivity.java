package com.ctrip.protoshop;

import java.util.Map;
import org.json.JSONObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.HttpCallback;
import com.ctrip.protoshop.http.HttpUtil;
import com.ctrip.protoshop.http.PostRequest;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;

public class BaseActivity extends Activity {
    /**
     * 登出广播，Setting页面登出后，进入登陆页面。其他页面自动关闭。
     */
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, new IntentFilter(Constans.LOGOUT));
    }

    /**
     * 
     * 发送HTTP,GET 请求.
     * 
     * @param function:发送请求的功能类型
     * @param callback:网络回调函数
     */
    public void sendGetRequest(Function function, final HttpCallback callback) {
        callback.onHttpStart();
        JsonObjectRequest request = new JsonObjectRequest(Method.GET, HttpUtil.getUrlByFunction(this, function), null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onErrorResponse(error);
                }
            });
        ProtoshopApplication.getInstance().requestQueue.add(request);
    }

    /**
     * 
     * 发送有参数的网络GET请求
     * 
     * @param function:发送请求的功能类型
     * @param params:需要添加的参数
     * @param callback:网络回调函数
     */
    public void sendGetParamRequest(Function function, Map<String, String> params, final HttpCallback callback) {
        callback.onHttpStart();
        String url = HttpUtil.getUrlByFunction(this, function) + Util.spliceParam(params);
        ProtoshopLog.e("GET_URL", url);
        JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }
        });
        ProtoshopApplication.getInstance().requestQueue.add(request);
    }

    /**
     * 
     * Post 网络请求
     * 
     * @param function
     * @param postParam
     * @param callback
     */
    public void sendPostRequest(Function function, Map<String, String> postParam, final HttpCallback callback) {
        callback.onHttpStart();
        PostRequest request = new PostRequest(HttpUtil.getUrlByFunction(this, function), postParam,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onErrorResponse(error);
                }
            });
        ProtoshopApplication.getInstance().requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
    }
}
