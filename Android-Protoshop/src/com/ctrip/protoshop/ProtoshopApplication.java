package com.ctrip.protoshop;

import java.util.ArrayList;
import java.util.List;
import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ctrip.protoshop.model.UserInfo;
import com.ctrip.protoshop.util.ProtoshopLog;
import com.protoshop.lua.LuaConfig;

public class ProtoshopApplication extends Application implements LuaConfig {
    public RequestQueue requestQueue;
    private static ProtoshopApplication instance;
    public List<String> mScences = new ArrayList<String>();

    public String token = "";
    public String userName = "";
    public UserInfo userInfo = new UserInfo();
    
    public String cachePath;

    public static ProtoshopApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        ProtoshopLog.e("into---[onCreate]");
        instance = this;
        requestQueue = Volley.newRequestQueue(this);
        ProtoshopLog.e("out---[onCreate]");
    }

    @Override
    public String getFloderPath() {

        return cachePath;
    }

}
