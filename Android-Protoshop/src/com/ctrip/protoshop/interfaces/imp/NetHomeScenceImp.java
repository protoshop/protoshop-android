package com.ctrip.protoshop.interfaces.imp;

import android.content.Context;
import com.ctrip.protoshop.interfaces.IHomeScence;
import com.ctrip.protoshop.util.Util;

public class NetHomeScenceImp implements IHomeScence {

    @Override
    public String getHomeSccence(Context context,String appID) {
        return Util.readFileContent(Util.getAppFloder(appID), "patch.lua");
    }

}
