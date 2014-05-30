package com.ctrip.protoshop.interfaces.imp;

import java.io.IOException;
import org.json.JSONException;
import android.content.Context;
import com.ctrip.protoshop.interfaces.IHomeScence;
import com.protoshop.lua.util.ParseJsonUtil2;

public class LocalParseHomeScenceImp implements IHomeScence {

    @Override
    public String getHomeSccence(Context context, String appID) {

        try {
            return ParseJsonUtil2.ParseJson(context, appID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
