package com.ctrip.protoshop.widget;

import com.ctrip.protoshop.util.Util;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class IconView extends TextView {

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Util.getIconTypeface(context));
    }
}
