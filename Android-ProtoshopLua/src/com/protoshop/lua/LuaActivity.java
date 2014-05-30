package com.protoshop.lua;

import java.util.List;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.TextView;
import com.protoshop.lua.Constant.Constants;
import com.protoshop.lua.cache.ScenceCache;
import com.protoshop.lua.util.LuaLog;
import com.protoshop.lua.util.Util;

public class LuaActivity extends Activity {

    private LuaState mLuaState;

    private String mScene;
    private String mAppID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LuaLog.e("LuaActivity", "into--[onCreate]");
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mScene = intent.getStringExtra(Constants.SCENE);
        mAppID = intent.getStringExtra(Constants.APPID);

        String luaStr = Util.getLusStr(this,mAppID, mScene + ".lua");

        if (TextUtils.isEmpty(luaStr)) {
            TextView textView = new TextView(this);
            textView.setText("Lua解析错误!");
            setContentView(textView);
            return;
        }

        LuaLog.e(luaStr); 

        try {
            mLuaState = LuaStateFactory.newLuaState();
            mLuaState.LdoString(luaStr);
            mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "onCreate");
            mLuaState.pushJavaObject(this);
            mLuaState.call(1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LuaLog.e("LuaActivity", "out--[onCreate]");

        IntentFilter filter = new IntentFilter(mScene);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "onResume");
//        mLuaState.pushJavaObject(this);
//        mLuaState.call(1, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        LuaLog.e("into---[onStop]");
//        mLuaState.getField(LuaState.LUA_GLOBALSINDEX, "onStop");
//        mLuaState.pushJavaObject(this);
//        mLuaState.call(1, 0);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            LuaActivity.this.finish();

        }
    };

    @Override
    public void startActivity(Intent intent) {

        String scenceId = intent.getStringExtra(Constants.SCENE);

        if (ScenceCache.getInstance().scences.contains(scenceId)) {

            List<String> scences = ScenceCache.getInstance().scences;
            List<String> subList = scences.subList(scences.indexOf(scenceId) + 1, scences.size());

            for (String string : subList) {
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(string));
            }
            //finish();
        } else {
            ScenceCache.getInstance().scences.add(scenceId);
            super.startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }

    @Override
    public void finish() {
        super.finish();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        ScenceCache.getInstance().scences.remove(mScene);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
