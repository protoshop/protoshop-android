package com.protoshop.lua.wiget;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import com.protoshop.lua.LuaBitmapUtil;
import com.protoshop.lua.interfaces.ILuaView;

@SuppressWarnings("deprecation")
public class LuaImageView extends AbsoluteLayout implements ILuaView {

    private float wPercent;
    private float hPercent;

    public LuaImageView(Context context) {
        this(context, null);
    }

    public LuaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dMetrics = context.getResources().getDisplayMetrics();
        wPercent = dMetrics.widthPixels / 400f;
        hPercent = dMetrics.heightPixels / 640f;
    }

    public LuaImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAttr(String appID, ViewGroup parent, ILuaView luaView, String attrJson, String lable) {
        try {

            View view = (View) luaView;
            JSONObject attrObject = new JSONObject(attrJson);
            int width = 0;
            if (attrObject.has("width")) {
                width = (int) (attrObject.getInt("width") * wPercent);
            }

            int height = 0;
            if (attrObject.has("height")) {
                height = (int) (attrObject.getInt("height") * hPercent);
            }

            int x = 0;
            if (attrObject.has("posX")) {
                x = (int) (attrObject.getInt("posX") * wPercent);
            }

            int y = 0;
            if (attrObject.has("posY")) {
                y = (int) (attrObject.getInt("posY") * hPercent);
            }

            String imageStr;
            if (attrObject.has("image")) {
                view.setBackgroundColor(Color.WHITE);
                imageStr = attrObject.getString("image");
                if (!TextUtils.isEmpty(imageStr)) {
                    BitmapDrawable drawable = LuaBitmapUtil.getBitmapDrawable(view.getContext(), appID, imageStr);
                    view.setBackgroundDrawable(drawable);
                }
            }

            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width, height, x, y);
            parent.addView(view, params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAction(String appID, View view, String actionJson) {
    }
}
