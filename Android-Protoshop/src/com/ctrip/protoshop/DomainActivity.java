package com.ctrip.protoshop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.OnHttpListener;
import com.ctrip.protoshop.util.ProtoshopLog;

public class DomainActivity extends BaseActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);

        mWebView = (WebView) findViewById(R.id.domain_webView);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.loadUrl("http://protoshop.ctripqa.com/ProtoShop/SSOLogin");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager cookieManager = CookieManager.getInstance();
                String cookieStr = cookieManager.getCookie(url);
                ProtoshopLog.e("webView", url);
                if (url.equals("http://protoshop.ctripqa.com/ProtoShop/SSOAuthCallBack")
                    && !TextUtils.isEmpty(cookieStr)) {
                    getUserInfo(url, cookieStr);
                }
            }

        });

    }

    protected void getUserInfo(String url, String cookieStr) {
        ProtoshopLog.e("into--[getUserInfo]");

        setCookieRequest(Function.DOMAIN_CALLBACK, new OnHttpListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ProtoshopLog.e(error.toString());
            }

            @Override
            public void onResponse(JSONObject response) {
                ProtoshopLog.e("Cookie", response.toString());
                try {
                    dealLoginResult(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHttpStart() {

            }
        });

    }

    /**
     * 
     * 处理登陆成功后的返回结果
     * @param response
     * @return
     * @throws JSONException
     */

    private String dealLoginResult(JSONObject response) throws JSONException {

        String resultStr = null;
        JSONArray resultArray = response.getJSONArray("result");
        JSONObject tokenObject = resultArray.getJSONObject(0);
        if (tokenObject.has("token")) {

            resultStr = "认证成功!";

            String token = tokenObject.getString("token");
            String name = tokenObject.getString("email");
            SharedPreferences preferences = getSharedPreferences(Constans.USER_INFO, 0);
            Editor editor = preferences.edit();
            editor.putString(Constans.TOKEN, token);
            editor.putString(Constans.USER_NAME, name);
            editor.commit();

            ProtoshopApplication.getInstance().token = token;
            ProtoshopApplication.getInstance().userName = name;

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);
            setResult(RESULT_OK);
            finish();

        }

        return resultStr;
    }
}
