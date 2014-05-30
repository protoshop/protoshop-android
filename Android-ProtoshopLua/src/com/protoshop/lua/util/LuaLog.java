package com.protoshop.lua.util;

import android.util.Log;

public class LuaLog {
    public static boolean debug = true;

    public static void e(String msg) {
        if (debug) {
            Log.e("debug", msg);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(tag, msg);
        }
    }
}
