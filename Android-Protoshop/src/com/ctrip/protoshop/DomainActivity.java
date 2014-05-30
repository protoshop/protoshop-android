package com.ctrip.protoshop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.util.ProtoshopLog;

public class DomainActivity extends Activity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);

        mWebView = (WebView) findViewById(R.id.domain_webView);
        mWebView.loadUrl("http://protoshop.ctripqa.com/ProtoShop/SSOLogin");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                ProtoshopLog.e(url);
                if (url.equals(getString(R.string.domain_callback_url))) {

                    view.loadUrl("javascript:window.local_obj.showSource("
                        + "document.getElementsByTagName('pre')[0].innerHTML);");
                }

            }

        });

    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String jsonStr) {
            ProtoshopLog.e(jsonStr);
            try {
                JSONObject response = new JSONObject(jsonStr);
                String resultStr = "认证失败!";

                try {
                    String status = "";

                    if (response.has("status")) {
                        status = response.getString("status");
                        if ("0".equals(status) && response.has("result")) {
                            resultStr = dealLoginResult(response);
                        } else if ("1".equals(status) && response.has("message")) {
                            resultStr = response.getString("message");
                        } else {
                            resultStr = "服务器错误，请联系开发人员!";
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    resultStr = "服务器返回错误!";
                }

                Toast.makeText(getApplicationContext(), resultStr, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
