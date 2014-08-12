package com.ctrip.protoshop.mini;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.protoshop.mini.model.PageModel;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.protoshop.lua.LuaConfig;

public class MiniCache implements LuaConfig {
	private static MiniCache instance=new MiniCache();
	public ProjectModel currentProjectModel;
	public PageModel pageModel;

	public String cachePath;

	public List<String> scenes = new ArrayList<String>();

	public static MiniCache getInstance() {
		return instance;
	}

	@Override
	public String getFloderPath() {
		return cachePath;
	}
}
