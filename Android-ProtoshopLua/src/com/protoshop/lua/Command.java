package com.protoshop.lua;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import com.protoshop.lua.factory.LuaViewFactory;
import com.protoshop.lua.interfaces.ILuaView;
import com.protoshop.lua.util.LuaLog;

@SuppressWarnings("deprecation")
public class Command {

    private static String appID = "";

    /**
     * 
     * 创建Scene,Lua中调用.
     * @param activity
     * @param applicationID：工程的ID
     * @param background:scene的背景图片，默认为白色
     * @return 创造成功后的Scene
     */
    public static ViewGroup createScence(Activity activity, String applicationID, String background) {
        LuaLog.e("into---[createScence]");

        appID = applicationID;

        AbsoluteLayout scenceLayout = new AbsoluteLayout(activity);
        scenceLayout.setBackgroundColor(Color.WHITE);
        if (!TextUtils.isEmpty(background)) {
            BitmapDrawable drawable = LuaBitmapUtil.getBitmapDrawable(activity, applicationID, background);
            if (drawable == null) {
                scenceLayout.setBackgroundColor(Color.WHITE);
            }else{
            	scenceLayout.setBackgroundDrawable(drawable);
            }
        }
        activity.setContentView(scenceLayout);

        LuaLog.e("out---[createScence]");
        return scenceLayout;
    }

    /**
     * 
     * 创建控件
     * @param context
     * @param viewType
     * @return
     */
    public static View createView(Context context, String viewType) {
        return (View) LuaViewFactory.createLuaView(context, viewType);
    }

    /**
     * 
     * 设置控件属性
     * @param parent 要设置属性控件的父容器
     * @param view 设置属性的控件
     * @param attrJson 控件属性的json 字符串
     * @param label textView中文字体
     */
    public static void setAttr(AbsoluteLayout parent, ILuaView view, String attrJson, String label) {
        view.setAttr(appID, parent, view, attrJson, label);
    }

    /**
     * 
     * 设置控件的点击事件
     * @param view
     * @param actionJson
     */
    public static void setAction(ILuaView view, String actionJson) {
        view.setAction(appID, (View) view, actionJson);
    }
}
