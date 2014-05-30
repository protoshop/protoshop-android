package com.protoshop.lua;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.protoshop.lua.util.LuaLog;
import com.protoshop.lua.wiget.LuaImageView;

@SuppressWarnings("deprecation")
public class Command {

    private static float wPercent = 1.0f;
    private static float hPercent = 1.0f;
    private static String appID = "";

    public static ViewGroup createScence(Activity activity, String applicationID, String background) {
        LuaLog.e("into---[createScence]");
        DisplayMetrics dMetrics = activity.getResources().getDisplayMetrics();
        wPercent = dMetrics.widthPixels / 400f;
        hPercent = dMetrics.heightPixels / 640f;

        appID = applicationID;

        AbsoluteLayout scenceLayout = new AbsoluteLayout(activity);
        if (!TextUtils.isEmpty(background)) {
            BitmapDrawable drawable = LuaBitmapUtil.getBitmapDrawable(activity, applicationID, background);
            if (drawable == null) {
                scenceLayout.setBackgroundColor(Color.WHITE);
            } else {
                scenceLayout.setBackgroundDrawable(drawable);
            }

        } else {
            scenceLayout.setBackgroundColor(Color.WHITE);
        }
        activity.setContentView(scenceLayout);

        LuaLog.e("out---[createScence]");
        return scenceLayout;
    }

    public static View createView(Context context, String className) {
        LuaLog.e("into--- " + className + "  [createView]");
        if (TextUtils.isEmpty(className)) {
            throw new NullPointerException("The View type is null!");
        }
        View view = null;
        if (className.equalsIgnoreCase("hotspot")) {
            view = new View(context);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else if (className.equalsIgnoreCase("button")) {
            view = new Button(context);
            //view.setBackgroundColor(Color.BLUE);
        } else if (className.equalsIgnoreCase("imageview")) {
            view = new LuaImageView(context);
        } else if (className.equalsIgnoreCase("label")) {
            view = new TextView(context);
        } else if (className.equalsIgnoreCase("ScrollView")) {
            view = new AbsoluteLayout(context);
        }
        LuaLog.e("out---[createView]");
        return view;
    }

    public static void setAttr(AbsoluteLayout parent, View view, String attrJson, String lable) {
        LuaLog.e("into---  " + view.getClass().getSimpleName() + "  [setAttr]");

        try {

            System.out.println(attrJson);

            JSONObject attrObject = new JSONObject(attrJson);
            int width;
            if (attrObject.has("width")) {
                width = (int) (attrObject.getInt("width") * wPercent);
            } else {
                LuaLog.e(attrJson);
                width = 480;
            }

            int height;
            if (attrObject.has("height")) {
                height = (int) (attrObject.getInt("height") * hPercent);
            } else {
                LuaLog.e(attrJson);
                height = 100;
            }

            int x;
            if (attrObject.has("x")) {
                x = (int) (attrObject.getInt("x") * wPercent);
            } else {
                LuaLog.e(attrJson);
                x = 100;
            }

            int y;
            if (attrObject.has("y")) {
                y = (int) (attrObject.getInt("y") * hPercent);
            } else {
                LuaLog.e(attrJson);
                y = 100;
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
                    int childWidth = jsonObject.getInt("contentSizeWidth");
                    int childHeight = jsonObject.getInt("contentSizeHeight");
                    scrollView.addView(view, new FrameLayout.LayoutParams(childWidth, childHeight));
                } else {
                    scrollView.addView(view, new FrameLayout.LayoutParams(width, height));
                }
                scrollView.setBackgroundColor(Color.WHITE);
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width, height, x, y);
                parent.addView(scrollView, params);
                return;
            }

            if (view instanceof LuaImageView) {
                setImageViewAttr((LuaImageView) view, attrObject);

            } else if (view instanceof TextView) {
                setTextViewAttr((TextView) view, attrObject, lable);
            }

            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(width, height, x, y);
            parent.addView(view, params);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LuaLog.e("out---[setAttr]");
    }

    private static void setTextViewAttr(TextView textView, JSONObject attrObject, String lable) throws JSONException {
        LuaLog.e("into--[setTextViewAttr]");
        if (attrObject.has("textFontSize")) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrObject.getInt("textFontSize"));
        }

        if (attrObject.has("text")) {

            String str = attrObject.getString("text");
            if (str != null) {
                if(lable==null){
                    throw new NullPointerException("lable is null!");
                }
                textView.setText(lable);
            }
        }

        if (attrObject.has("textColor")) {
            JSONObject fontColorObject = attrObject.getJSONObject("textColor");
            textView.setTextColor(createColor(fontColorObject));
        }

        if (attrObject.has("bgColor")) {
            JSONObject backColorObject = attrObject.getJSONObject("bgColor");
            textView.setBackgroundColor(createColor(backColorObject));
        }
        LuaLog.e("out--[setTextViewAttr]");
    }

    private static int createColor(JSONObject colorObject) throws JSONException {
        LuaLog.e("into--[createColor]");
        int red = (int) (colorObject.getDouble("bkColorRed") * 255);
        int green = (int) (colorObject.getDouble("bkColorGreen") * 255);
        int blue = (int) (colorObject.getDouble("bkColorBlue") * 255);
        int alpha = (int) (colorObject.getDouble("bkAlpha") * 100);
        LuaLog.e("out--[createColor]");
        return Color.argb(alpha, red, green, blue);
    }

    private static void setImageViewAttr(LuaImageView luaImageView, JSONObject attrObject) throws JSONException {
        LuaLog.e("into--[setImageViewAttr]");
        if (attrObject.has("imageName")) {
            String imageStr = attrObject.getString("imageName");
            if (!TextUtils.isEmpty(imageStr)) {
                BitmapDrawable drawable = LuaBitmapUtil.getBitmapDrawable(luaImageView.getContext(), appID, imageStr);
                if (drawable == null) {
                    luaImageView.setBackgroundColor(Color.WHITE);
                } else {
                    luaImageView.setBackgroundDrawable(drawable);
                }
            } else {
                luaImageView.setBackgroundColor(Color.WHITE);
            }
        }
        LuaLog.e("out--[setImageViewAttr]");
    }

    public static void setAction(View view, String actionJson) {
        LuaLog.e("int---[setAction]");
        final Context context = view.getContext();
        try {
            JSONObject actionObject = new JSONObject(actionJson);
            final String target = actionObject.getString("target");
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
}
