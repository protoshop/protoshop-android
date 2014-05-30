package com.ctrip.protoshop;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.HttpCallback;
import com.ctrip.protoshop.util.MD5Util;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;

public class ChangePswActivity extends BaseActivity implements OnClickListener, HttpCallback {

    private View mBackView;
    private View mSaveView;
    private View mProgressView;

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

        mBackView = findViewById(R.id.change_psw_back_view);
        mBackView.setOnClickListener(this);

        mCurView = (EditText) findViewById(R.id.current_psw_view);
        mNewView = (EditText) findViewById(R.id.new_psw_view);
        mRepeatView = (EditText) findViewById(R.id.retype_psw_view);

        mSaveView = findViewById(R.id.change_save_btn);
        mSaveView.setOnClickListener(this);

        mProgressView = findViewById(R.id.change_psw_loading_view);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.change_psw_back_view) {
            finish();
        } else if (id == R.id.change_save_btn) {
            dealSaveChage(v);
        }
    }

    /**
     * 
     * 处理修改密码.
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

        mProgressView.setVisibility(View.VISIBLE);
        Util.hideSoftkeyboard(getApplicationContext(), view);

        HashMap<String, String> postParams = new HashMap<String, String>();
        postParams.put("token", ProtoshopApplication.getInstance().token);
        postParams.put("passwd", MD5Util.getMd5(mNewPswStr));
        postParams.put("oldpwd", MD5Util.getMd5(mCurPswStr));

        sendPostRequest(Function.CHANGE_PSW, postParams, this);

    }

    /**
     * 修改密码回调函数
     */
    @Override
    public void onResponse(JSONObject response) {
        ProtoshopLog.e(response.toString());
        mProgressView.setVisibility(View.GONE);
        String resultStr = "修改成功";
        try {

            String status;
            if (response.has("status")) {
                status = response.getString("status");
                if ("1".equals(status) && response.has("code")) {

                    String code = response.getString("code");
                    if ("3002".equals(code)) {
                        resultStr = "请重新登陆!";
                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constans.LOGOUT));
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
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "网络错误，请稍后再试!", Toast.LENGTH_SHORT).show();
        mProgressView.setVisibility(View.GONE);
        ProtoshopLog.e("error", error.toString());
    }

    @Override
    public void onHttpStart() {
    }

}
