package com.protoshop.lua.factory;

import android.content.Context;
import android.text.TextUtils;
import com.protoshop.lua.interfaces.ILuaView;
import com.protoshop.lua.util.LuaLog;
import com.protoshop.lua.wiget.LuaImageView;
import com.protoshop.lua.wiget.LuaLabelView;
import com.protoshop.lua.wiget.LuaScrollView;
import com.protoshop.lua.wiget.LuaTextView;
import com.protoshop.lua.wiget.LuaView;

public class LuaViewFactory {
    public static ILuaView createLuaView(Context context, String type) {
        if (TextUtils.isEmpty(type)) {
            throw new NullPointerException("type is not valid!");
        }
        ILuaView luaView = null;
        if (type.equalsIgnoreCase("button") || type.equalsIgnoreCase("hotspot")) {
            luaView = new LuaTextView(context);
        } else if (type.equalsIgnoreCase("label")) {
            luaView = new LuaLabelView(context);
        } else if (type.equalsIgnoreCase("imageview")) {
            luaView = new LuaImageView(context);
        } else if (type.equalsIgnoreCase("ScrollView")) {
            luaView = new LuaScrollView(context);
        } else if (type.equalsIgnoreCase("view")) {
            luaView=new LuaView(context);
        } else {
            LuaLog.e("This type is not suppored!");
            throw new RuntimeException("this type is not suppored!");
        }
        return luaView;
    }
}
