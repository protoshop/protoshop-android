package com.protoshop.lua.wiget;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import com.protoshop.lua.LuaActivity;
import com.protoshop.lua.interfaces.ILuaView;
import com.protoshop.lua.util.LuaLog;

@SuppressWarnings("deprecation")
public class LuaTextView extends TextView implements ILuaView {

    private float wPercent;
    private float hPercent;

    public LuaTextView(Context context) {
        this(context, null);
    }

    public LuaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dMetrics = context.getResources().getDisplayMetrics();
        wPercent = dMetrics.widthPixels / 400f;
        hPercent = dMetrics.heightPixels / 640f;
        setGravity(Gravity.CENTER);
    }

    public LuaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAttr(String appID, ViewGroup parent, ILuaView view, String attrJson, String lable) {
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
            setTextViewAttr((TextView) view, attrObject, lable);

            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width, height, x, y);
            parent.addView((View) view, params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAction(final String appID, View view, String actionJson) {

        LuaLog.e("int---[setAction]");
        final Context context = view.getContext();
        try {
            JSONObject actionObject = new JSONObject(actionJson);
            final String target = actionObject.getString("target");
            if (TextUtils.isEmpty(target)) {
                return;
            }
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LuaActivity.class);
                    intent.putExtra("scene", target);
                    intent.putExtra("appID", appID);
                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LuaLog.e("out---[setAction]");

    }

    private void setTextViewAttr(TextView textView, JSONObject attrObject, String lable) throws JSONException {
        LuaLog.e("into--[setTextViewAttr]");
        if (attrObject.has("textSize")) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrObject.getInt("textSize"));
        }

        if (!TextUtils.isEmpty(lable)) {
            textView.setText(lable);
        }

        if (attrObject.has("textColor")) {
            String colorStr = attrObject.getString("textColor");
            textView.setTextColor(createColor(colorStr));
        }

        if (attrObject.has("bgColor")) {
            String colorStr = attrObject.getString("bgColor");
            textView.setBackgroundColor(createColor(colorStr));
        }

        if (attrObject.has("bgOpacity")) {
            textView.setBackgroundColor(Color.TRANSPARENT);
        }

        LuaLog.e("out--[setTextViewAttr]");
    }

    private int createColor(String colorStr) throws JSONException {
        LuaLog.e("into--[createColor]");
        colorStr = colorStr.replace("$", "#");
        LuaLog.e("out--[createColor]");
        return Color.parseColor(colorStr);
    }

}
