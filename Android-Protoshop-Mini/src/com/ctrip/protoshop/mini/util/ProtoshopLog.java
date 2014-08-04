package com.ctrip.protoshop.mini.util;

import android.util.Log;

public class ProtoshopLog {
    private static boolean debug = true;

    public static void e(String tag, String msg) {
        if (!debug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (!debug) {
            Log.e("debug", msg);
        }
    }
}
