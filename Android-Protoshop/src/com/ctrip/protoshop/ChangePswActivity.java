package com.ctrip.protoshop;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.util.MD5Util;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;
import com.ctrip.protoshop.widget.HttpAsyncLayout;
import com.ctrip.protoshop.widget.HttpAsyncLayout.OnHttpAsyncListner;

public class ChangePswActivity extends BaseActivity {

	private HttpAsyncLayout mAsyncLayout;
	private EditText mCurView;
	private EditText mNewView;
	private EditText mRepeatView;

	private String mCurPswStr;
	private String mNewPswStr;
	private String mRepeatPswStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_psw);

		initUI();

	}

	private void initUI() {

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Change Password");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		mAsyncLayout = (HttpAsyncLayout) findViewById(R.id.http_async_layout);

		mCurView = (EditText) findViewById(R.id.current_psw_view);
		mNewView = (EditText) findViewById(R.id.new_psw_view);
		mRepeatView = (EditText) findViewById(R.id.retype_psw_view);

		addOnListener();
	}

	private void addOnListener() {
		mAsyncLayout.setOnHttpAsyncListner(new OnHttpAsyncListner() {

			@Override
			public void onSuccessListener(String response) {
				ProtoshopLog.e(response);

				String resultStr = "修改成功";
				try {
					JSONObject resultObject = new JSONObject(response);
					String status;
					if (resultObject.has("status")) {
						status = resultObject.getString("status");
						if ("1".equals(status) && resultObject.has("code")) {

							String code = resultObject.getString("code");
							if ("3002".equals(code)) {
								resultStr = "请重新登陆!";
								LocalBroadcastManager.getInstance(ChangePswActivity.this).sendBroadcast(new Intent(Constans.LOGOUT));
								startActivity(new Intent(getApplicationContext(), LoginActivity.class));
								finish();
							} else if ("3005".equals(code)) {
								resultStr = "服务器错误，请稍后再试!";
							} else if ("3004".equals(code)) {
								resultStr = "旧密码错误!";
							} else {
								resultStr = "未知错误,请联系开发者!";
							}

						} else if ("0".equals(status)) {
							resultStr = "修改密码成功!";
							finish();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					resultStr = "解析出错!";
				}

				Toast.makeText(getApplicationContext(), resultStr, Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onRefreshListener() {
			}

			@Override
			public void onErrorListener(VolleyError error) {

				Toast.makeText(getApplicationContext(), "网络错误，请稍后再试!", Toast.LENGTH_SHORT).show();
				ProtoshopLog.e("error", error.toString());

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.change_password, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.ic_action_save) {
			dealSaveChage(findViewById(R.id.ic_action_save));
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * 处理修改密码.
	 * 
	 * @param view
	 */
	private void dealSaveChage(View view) {
		mCurPswStr = mCurView.getText().toString();
		if (TextUtils.isEmpty(mCurPswStr)) {
			Toast.makeText(getApplicationContext(), "请填写旧密码!", Toast.LENGTH_SHORT).show();
			return;
		}

		mNewPswStr = mNewView.getText().toString();
		if (TextUtils.isEmpty(mNewPswStr)) {
			Toast.makeText(getApplicationContext(), "请填写新密码!", Toast.LENGTH_SHORT).show();
			return;
		}

		mRepeatPswStr = mRepeatView.getText().toString();
		if (!mNewPswStr.equals(mRepeatPswStr)) {
			Toast.makeText(getApplicationContext(), "两次密码不相同!", Toast.LENGTH_SHORT).show();
			return;
		}

		Util.hideSoftkeyboard(getApplicationContext(), view);

		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("token", ProtoshopApplication.getInstance().token);
		postParams.put("passwd", MD5Util.getMd5(mNewPswStr));
		postParams.put("oldpwd", MD5Util.getMd5(mCurPswStr));

		sendPostRequest(Function.CHANGE_PSW, postParams, mAsyncLayout);

	}

}
