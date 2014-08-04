package com.ctrip.protoshop.mini;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.protoshop.lua.LuaConfig;

import android.app.Application;

public class MiniApplication extends Application implements LuaConfig{
    private static MiniApplication instance;
    public ProjectModel currentProjectModel;
    public PageModel pageModel;
    
    public String cachePath;
    
    public List<String> scenes = new ArrayList<String>();

    public static MiniApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
    }

    @Override
    public String getFloderPath() {
        return cachePath;
    }
}
