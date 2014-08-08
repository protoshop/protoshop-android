package com.ctrip.protoshop.mini.model;

import java.util.List;

public class ProjectModel {
	public String appID;
	public String appName;
	public String appAnimType="0";
	public String homeScene;
	public String pageNum = "0";
	public String appPlatform = "android";
	public boolean isDelete = false;
	public List<PageModel> scenes;
}
