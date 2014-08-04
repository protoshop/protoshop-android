package com.ctrip.protoshop.mini.wiget;

import com.ctrip.protoshop.mini.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MiniTitleView extends RelativeLayout {
    private String leftText;
    private String rightText;
    private String centerText;
    private TextView leftView;
    private TextView centerView;
    private TextView rightView;

    private OnTitleClikListener onTitleClikListener;

    public interface OnTitleClikListener {
        public void onLeftClik(View view);

        public void onCenterClik(View view);

        public void onRightClik(View view);
    }

    public MiniTitleView(Context context) {
        this(context, null);
    }

    public MiniTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        int attrCount = attrArray.getIndexCount();
        for (int i = 0; i < attrCount; i++) {
            int attr = attrArray.getIndex(i);
            switch (attr) {
            case R.styleable.TitleView_left_text:
                leftText = attrArray.getString(R.styleable.TitleView_left_text);
                break;
            case R.styleable.TitleView_center_text:
                centerText = attrArray.getString(R.styleable.TitleView_center_text);
                break;
            case R.styleable.TitleView_right_text:
                rightText = attrArray.getString(R.styleable.TitleView_right_text);
                break;
            default:
                break;
            }
        }
        attrArray.recycle();

        setBackgroundColor(getResources().getColor(R.color.blue_014eb6));
        initView(context);

    }

    private void initView(Context context) {
        inflate(context, R.layout.app_title_layout, this);

        leftView = (TextView) findViewById(R.id.title_left_view);
        leftView.setVisibility(View.GONE);
        centerView = (TextView) findViewById(R.id.title_center_view);
        centerView.setVisibility(View.GONE);
        rightView = (TextView) findViewById(R.id.title_right_view);
        rightView.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(leftText)) {
            leftView.setText(leftText);
            leftView.setVisibility(View.VISIBLE);
            leftView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onTitleClikListener != null) {
                        onTitleClikListener.onLeftClik(v);
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(centerText)) {
            centerView.setText(centerText);
            centerView.setVisibility(View.VISIBLE);
            centerView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onTitleClikListener.onCenterClik(v);
                }
            });
        }

        if (!TextUtils.isEmpty(rightText)) {
            rightView.setText(rightText);
            rightView.setVisibility(View.VISIBLE);
            rightView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onTitleClikListener.onRightClik(v);
                }
            });
        }
    }

    public void updateCenterText(String text) {
        centerText = text;
        centerView.setText(text);
        centerView.setVisibility(View.VISIBLE);

    }

    public OnTitleClikListener getOnTitleClikListener() {
        return onTitleClikListener;
    }

    public void setOnTitleClikListener(OnTitleClikListener onTitleClikListener) {
        this.onTitleClikListener = onTitleClikListener;
    }

}
