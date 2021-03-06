package com.ctrip.protoshop;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class SignUpActivity extends BaseActivity {

	private HttpAsyncLayout mAsyncLayout;
	private AutoCompleteTextView mNameView;
	private EditText mPswView;
	private View mSignUpView;

	private String mNameStr;
	private String mPswStr;

	private String[] mEmails = { "@ctrip.com", "@163.com", "@qq.com" };
	private ArrayAdapter<String> mEmailAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("Sign Up");

		mAsyncLayout = (HttpAsyncLayout) findViewById(R.id.http_async_layout);

		mNameView = (AutoCompleteTextView) findViewById(R.id.sigin_up_name_view);
		mPswView = (EditText) findViewById(R.id.sign_up_psw_view);
		mSignUpView = findViewById(R.id.sign_up_btn_view);

		mEmailAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		mNameView.setAdapter(mEmailAdapter);
		mNameView.setThreshold(1);

		addOnListener();
	}

	private void addOnListener() {
		mSignUpView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dealSignUp();
			}
		});
		mNameView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String string = s.toString();
				if (string.contains("@")) {
					string = string.substring(0, string.indexOf("@"));
				}
				mEmailAdapter.clear();
				for (String str : mEmails) {
					mEmailAdapter.add(string + str);
				}
				mEmailAdapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mAsyncLayout.setOnHttpAsyncListner(new OnHttpAsyncListner() {

			@Override
			public void onSuccessListener(String response) {
				ProtoshopLog.e(response.toString());
				String resultStr = "注册失败!";

				try {
					JSONObject resultObject = new JSONObject(response);
					String status = "";

					// status=0 注册成功，status=1注册失败。
					if (resultObject.has("status")) {
						status = resultObject.getString("status");
						if ("0".equals(status) && resultObject.has("result")) {
							JSONArray resultArray = resultObject.getJSONArray("result");
							JSONObject tokenObject = resultArray.getJSONObject(0);
							if (tokenObject.has("token")) {
								resultStr = "注册成功!";
								LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constans.SIGN_UP));

								String token = tokenObject.getString("token");
								SharedPreferences preferences = getSharedPreferences(Constans.USER_INFO, 0);
								Editor editor = preferences.edit();
								editor.putString(Constans.TOKEN, token);
								editor.putString(Constans.USER_NAME, mNameStr);
								editor.commit();

								ProtoshopApplication.getInstance().token = token;
								ProtoshopApplication.getInstance().userName = mNameStr;

								Intent intent = new Intent(getApplicationContext(), MainActivity.class);
								startActivity(intent);

								finish();
							}
						} else if ("1".equals(status) && resultObject.has("message")) {
							resultStr = resultObject.getString("message");

							// String code = response.getString("code");
							// if ("2002".equals(code)) {
							// resultStr = response.getString("message");
							// } else if ("2006".equals(code)) {
							// resultStr = "邮箱格式错误!";
							// }

						} else {
							resultStr = "服务器错误，请联系开发人员!";
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					resultStr = "服务器返回错误!";
				}

				Toast.makeText(getApplicationContext(), resultStr, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRefreshListener() {
			}

			@Override
			public void onErrorListener(VolleyError error) {
				ProtoshopLog.e("error", error.toString());
				Toast.makeText(getApplicationContext(), "网络错误，请稍后再试!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void dealSignUp() {

		mNameStr = mNameView.getText().toString();
		mPswStr = mPswView.getText().toString();

		if (TextUtils.isEmpty(mNameStr)) {
			Toast.makeText(getApplicationContext(), "请输入邮件地址", Toast.LENGTH_SHORT).show();
			return;
		} else {
			Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
			Matcher matcher = pattern.matcher(mNameStr);
			if (!matcher.matches()) {
				Toast.makeText(getApplicationContext(), "请输入正确的邮件地址", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if (TextUtils.isEmpty(mPswStr)) {
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}

		Util.hideSoftkeyboard(getApplicationContext(), mNameView);

		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("email", mNameStr);
		postParams.put("passwd", MD5Util.getMd5(mPswStr));
		sendPostRequest(Function.SIGN_Up, postParams, mAsyncLayout);
	}

}
