package com.protoshop.lua.wiget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

public class LuaLabelView extends LuaTextView {

    public LuaLabelView(Context context) {
        this(context,null);
    }

    public LuaLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

    public LuaLabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
