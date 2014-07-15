package com.ctrip.protoshop;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.OnHttpListener;
import com.ctrip.protoshop.util.ProtoshopLog;

public class SplashActivity extends BaseActivity implements Callback, OnHttpListener {

	private static final int DURATION_TIME = 1000;
	private static final int ANIMATION = 1111;
	private static final int LOGIN = 1112;

	private int msgType = ANIMATION;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mHandler = new Handler(this);

		SharedPreferences preferences = getSharedPreferences(Constans.USER_INFO, 0);

		ProtoshopApplication.getInstance().userName = preferences.getString(Constans.USER_NAME, null);
		String token = preferences.getString(Constans.TOKEN, null);

		if (!TextUtils.isEmpty(token)) {
			msgType = LOGIN;
			ProtoshopApplication.getInstance().token = token;
			// mHandler.sendEmptyMessageDelayed(LOGIN, DURATION_TIME);
			HashMap<String, String> postParams = new HashMap<String, String>();
			postParams.put("token", token);
			sendPostRequest(Function.USERINFO, postParams, this);
		} else {
			mHandler.sendEmptyMessageDelayed(ANIMATION, DURATION_TIME);
			msgType = ANIMATION;
		}

	}

	/**
	 * Handler 回调函数
	 */
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == ANIMATION) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return true;
		} else if (msg.what == LOGIN) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		return false;
	}

	/**
	 * 网络请求成功回调函数
	 */
	@Override
	public void onResponse(JSONObject response) {
		ProtoshopLog.e(response.toString());
		try {
			if (response.has("status")) {
				String statusStr = response.getString("status");
				if ("0".equals(statusStr) && response.has("result")) {
					JSONArray array = response.getJSONArray("result");
					JSONObject userObject = array.getJSONObject(0);
					if (userObject.has("email")) {
						ProtoshopApplication.getInstance().userInfo.email = userObject.getString("email");
					}
					if (userObject.has("name")) {
						ProtoshopApplication.getInstance().userInfo.name = userObject.getString("name");
					}
					if (userObject.has("nickname")) {
						ProtoshopApplication.getInstance().userInfo.nickname = userObject.getString("nickname");
					}
					startActivity(new Intent(this, MainActivity.class));
					finish();
				} else if ("1".equals(statusStr)) {
					if (response.has("code")) {
						String code = response.getString("code");
						if ("4001".equals(code) || "4002".equals(code) || "4003".equals(code)) {
							startActivity(new Intent(this, LoginActivity.class));
							finish();
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络请求失败回调函数
	 */
	@Override
	public void onErrorResponse(VolleyError error) {
		ProtoshopLog.e("error", error.toString());

		startActivity(new Intent(this, MainActivity.class));
		finish();

	}

	@Override
	public void onHttpStart() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(msgType);
	}
}
