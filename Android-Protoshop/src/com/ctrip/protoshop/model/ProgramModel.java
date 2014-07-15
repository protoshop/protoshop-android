package com.ctrip.protoshop.model;

import com.ctrip.protoshop.interfaces.IProgramState;

public class ProgramModel {
    public String appID;
    public String comment;
    public String editTime;
    public String createTime;
    public String icon;
    public String appName;
    public String isPublic;
    public boolean isLoadZip = false;
    public boolean isLoading = false;

    public IProgramState state;

    public String home_scence;
}
