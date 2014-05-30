package com.ctrip.protoshop;

import java.util.regex.Pattern;
import org.apache.http.util.EncodingUtils;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import com.ctrip.protoshop.util.Util;
import com.protoshop.lua.util.LuaLog;

public class TestActivity extends Activity {

    private LuaState mLuaState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LuaLog.e("LuaActivity", "into--[onCreate]");
        ProtoshopApplication.getInstance().cachePath = Util.getUserRootFile().getAbsolutePath();

        String luaStr = com.ctrip.protoshop.util.Util.readStream(getResources().openRawResource(R.raw.command));

        luaStr = EncodingUtils.getString(luaStr.getBytes(), "UTF-8");
        System.out.println(luaStr);

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

    }

}
