package com.ctrip.protoshop;

import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.volley.Request.Method;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.AppRequest;
import com.ctrip.protoshop.http.HttpUtil;
import com.ctrip.protoshop.http.OnHttpListener;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;

public class BaseActivity extends Activity {
	private final static String TAG = BaseActivity.class.getSimpleName();
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
	 * @param function
	 *            :发送请求的功能类型
	 * @param onHttpListener
	 *            :网络回调函数
	 */
	public void sendGetRequest(Function function, final OnHttpListener onHttpListener) {
		onHttpListener.onHttpStart();
		AppRequest request = new AppRequest(Method.GET, HttpUtil.getUrlByFunction(this, function), onHttpListener, onHttpListener);
		request.setTag(TAG);
		ProtoshopApplication.getInstance().requestQueue.add(request);
	}

	/**
	 * 
	 * 发送有参数的网络GET请求
	 * 
	 * @param function
	 *            :发送请求的功能类型
	 * @param params
	 *            :需要添加的参数
	 * @param onHttpListener
	 *            :网络回调函数
	 */
	public void sendGetParamRequest(Function function, Map<String, String> params, final OnHttpListener onHttpListener) {
		onHttpListener.onHttpStart();
		String url = HttpUtil.getUrlByFunction(this, function) + Util.spliceParam(params);
		ProtoshopLog.e("GET_URL", url);
		AppRequest request = new AppRequest(Method.GET, url, onHttpListener, onHttpListener);
		request.setTag(TAG);
		ProtoshopApplication.getInstance().requestQueue.add(request);
	}

	/**
	 * 
	 * Post 网络请求
	 * 
	 * @param function
	 * @param postParam
	 * @param onHttpListener
	 */
	public void sendPostRequest(Function function, Map<String, String> postParam, final OnHttpListener onHttpListener) {
		onHttpListener.onHttpStart();
		AppRequest request = new AppRequest(HttpUtil.getUrlByFunction(this, function), postParam, onHttpListener, onHttpListener);
		request.setTag(TAG);
		ProtoshopApplication.getInstance().requestQueue.add(request);
	}

	public void setCookieRequest(Function function, final OnHttpListener onHttpListener) {
		onHttpListener.onHttpStart();
		AppRequest request = new AppRequest(Method.GET, HttpUtil.getUrlByFunction(this, function), onHttpListener, onHttpListener);
		CookieSyncManager.createInstance(this).sync();
		request.setCookie(CookieManager.getInstance().getCookie("http://protoshop.ctripqa.com/ProtoShop/SSOLogin"));
		request.setTag(TAG);
		ProtoshopApplication.getInstance().requestQueue.add(request);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
	}
}
