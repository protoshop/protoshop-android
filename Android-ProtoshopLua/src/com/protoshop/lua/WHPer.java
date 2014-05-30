package com.protoshop.lua;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 
 * 为了在不同设备上运行Demo，对Demo中的width、height、坐标等做缩放。
 */
public class WHPer {
    //宽度缩放比例
    public float wPer;
    //高度缩放比例
    public float hPer;

    //针对网络Demo的缩放比例
    public WHPer(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wPer = dm.widthPixels / 400f;
        hPer = dm.heightPixels / 640f;
    }

    //针对mini的缩放比例
    public WHPer(Context context, float top) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        wPer = 1.0f;
        hPer = dm.heightPixels / (dm.heightPixels - top);
    }
}
