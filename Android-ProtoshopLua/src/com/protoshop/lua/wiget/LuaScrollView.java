package com.protoshop.lua.wiget;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import com.protoshop.lua.interfaces.ILuaView;

@SuppressWarnings("deprecation")
public class LuaScrollView extends AbsoluteLayout implements ILuaView {

    private float wPercent;
    private float hPercent;

    public LuaScrollView(Context context) {
        this(context, null);
    }

    public LuaScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dMetrics = context.getResources().getDisplayMetrics();
        wPercent = dMetrics.widthPixels / 400f;
        hPercent = dMetrics.heightPixels / 640f;
    }

    public LuaScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAttr(String appID, ViewGroup parent, ILuaView luaView, String attrJson, String lable) {
        View view = (View) luaView;
        try {

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
            if (attrObject.has("orientation")) {
                ViewGroup scrollView = null;
                String orientationStr = attrObject.getString("orientation");
                if (orientationStr.equalsIgnoreCase("horizontal")) {
                    scrollView = new HorizontalScrollView(view.getContext());
                } else if (orientationStr.equalsIgnoreCase("vertical")) {
                    scrollView = new ScrollView(view.getContext());
                }
                if (attrObject.has("contentSize")) {
                    JSONObject jsonObject = attrObject.getJSONObject("contentSize");
                    int childWidth = (int) (jsonObject.getInt("width") * wPercent);
                    int childHeight = (int) (jsonObject.getInt("height") * hPercent);
                    scrollView.addView(view, new FrameLayout.LayoutParams(childWidth, childHeight));
                } else {
                    scrollView.addView(view, new FrameLayout.LayoutParams(width, height));
                }
                scrollView.setBackgroundColor(Color.WHITE);
                scrollView.setHorizontalFadingEdgeEnabled(false);
                scrollView.setHorizontalScrollBarEnabled(false);
                scrollView.setVerticalFadingEdgeEnabled(false);
                scrollView.setVerticalScrollBarEnabled(false);
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width, height, x, y);
                parent.addView(scrollView, params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setAction(String appID, View view, String actionJson) {

    }

}
