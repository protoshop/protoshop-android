package com.ctrip.protoshop.interfaces.imp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.ctrip.protoshop.LoginActivity;
import com.ctrip.protoshop.ProtoshopApplication;
import com.ctrip.protoshop.R;
import com.ctrip.protoshop.adapter.ProgramAdapter;
import com.ctrip.protoshop.adapter.ProgramAdapter.ViewHolder;
import com.ctrip.protoshop.constans.Constans;
import com.ctrip.protoshop.constans.Function;
import com.ctrip.protoshop.http.AppRequest;
import com.ctrip.protoshop.http.FileReuest;
import com.ctrip.protoshop.http.HttpUtil;
import com.ctrip.protoshop.http.OnHttpListener;
import com.ctrip.protoshop.interfaces.IHomeScence;
import com.ctrip.protoshop.interfaces.IProgramState;
import com.ctrip.protoshop.model.ProgramModel;
import com.ctrip.protoshop.model.ZipModel;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.ctrip.protoshop.util.Util;

public class NormalState implements IProgramState {
    private Context mContext;
    private ProgramAdapter mAdapter;
    private IHomeScence mHomeScence;
    private Map<String, ProgramModel> mLoadMaps;

    public NormalState(Context context, ProgramAdapter adapter, IHomeScence homeScence,
                       Map<String, ProgramModel> loadMaps) {
        mContext = context;
        mAdapter = adapter;
        mHomeScence = homeScence;
        mLoadMaps = loadMaps;
    }

    @Override
    public void onItemClick(ProgramModel model) {
        loadZipInfo(model);
    }

    @Override
    public void showView(ViewHolder holder) {
        holder.arrowView.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
    }

    private void loadZipInfo(final ProgramModel programModel) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", programModel.appID);
        params.put("token", ProtoshopApplication.getInstance().token);
        ProtoshopLog.e("appId", programModel.appID);
        ProtoshopLog.e("token", ProtoshopApplication.getInstance().token);

        OnHttpListener onHttpListener = new OnHttpListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                programModel.state = NormalState.this;
                mAdapter.notifyDataSetChanged();
                ProtoshopLog.e("error", error.toString());
                if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext, "请连接网络!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponse(String response) {
                ProtoshopLog.e(response);
                programModel.state = new LoadedState(mContext);
                try {
                    JSONObject resultObject = new JSONObject(response);
                    if (resultObject.has("status")) {
                        String statusStr = resultObject.getString("status");
                        if ("0".equals(statusStr)) {
                            ZipModel zipModel = new ZipModel();
                            JSONArray resultArray = resultObject.getJSONArray("result");
                            JSONObject jsonObject = resultArray.getJSONObject(0);
                            try {
                                zipModel.url = jsonObject.getString("url");
                                Toast.makeText(mContext, "获取Zip信息成功,开始下载ZIP文件!", Toast.LENGTH_SHORT).show();
                                loadZipFile(programModel, zipModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, "解析JSON出错!", Toast.LENGTH_SHORT).show();
                            }

                        } else if ("1".equals(statusStr)) {
                            String code = resultObject.getString("code");
                            if ("15005".equals(code)) {
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constans.LOGOUT));
                                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                Toast.makeText(mContext, "请重新登陆!", Toast.LENGTH_SHORT).show();
                            } else if ("15003".equals(code) || "15002".equals(code)) {
                                Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHttpStart() {
                programModel.state = new LoadingState();
                mAdapter.notifyDataSetChanged();
            }
        };
        onHttpListener.onHttpStart();
        AppRequest request = new AppRequest(HttpUtil.getUrlByFunction(mContext, Function.ZIP), params, onHttpListener,
            onHttpListener);
        ProtoshopApplication.getInstance().requestQueue.add(request);
    }

    /**
     * 
     * 下载压缩文件.
     * 
     * @param 压缩包MODEL
     */
    private void loadZipFile(final ProgramModel programModel, final ZipModel zipModel) {

        OnHttpListener onHttpListener = new OnHttpListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                programModel.state = NormalState.this;
                mAdapter.notifyDataSetChanged();
                Toast.makeText(mContext, "获取压缩包失败!", Toast.LENGTH_SHORT).show();
                ProtoshopLog.e("error", error.toString());
            }

            @Override
            public void onResponse(String response) {
                programModel.state = new LoadedState(mContext);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(mContext, "Zip下载完成，开始解压!", Toast.LENGTH_SHORT).show();
                try {
                    Util.unZipFiles(response, Util.getUserRootFile().getAbsolutePath());
                    Util.deleteFile(new File(response));
                    mLoadMaps.put(programModel.appID, programModel);
                    mAdapter.notifyDataSetChanged();
                    // Toast.makeText(getApplicationContext(), "ZIP解压完成,开始生成Lua!", Toast.LENGTH_SHORT).show();
                    programModel.home_scence = mHomeScence.getHomeSccence(mContext, programModel.appID);
                    // mLoadingLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHttpStart() {
                ProtoshopLog.e(zipModel.url);
                if (!(programModel.state instanceof LoadingState)) {
                    programModel.state = new LoadingState();
                }
            }
        };

        onHttpListener.onHttpStart();
        FileReuest request = new FileReuest(zipModel.url, onHttpListener, onHttpListener);
        ProtoshopApplication.getInstance().requestQueue.add(request);
    }

}
