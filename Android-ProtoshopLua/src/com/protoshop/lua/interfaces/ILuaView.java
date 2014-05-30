package com.protoshop.lua.interfaces;

import android.view.View;
import android.view.ViewGroup;

public interface ILuaView {
    public void setAttr(String appID,ViewGroup parent, ILuaView view, String attrJson, String lable);

    public void setAction(String appID,View view, String actionJson);
}
