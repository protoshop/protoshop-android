package com.ctrip.protoshop.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.ctrip.protoshop.R;

public class AnimationButton extends FrameLayout implements OnClickListener, Callback {
    private String iconText = "";
    private String contentText = "";
    private String animationText = "";

    private TextView mAnimView;
    private Animation mAnimation;

    private TextView mResultView;
    private TextView mContentView;

    private View mView;

    private Handler mHandler;

    private OnConfirmLisntener onConfirmLisntener;

    public interface OnConfirmLisntener {
        public boolean onConfirm(View view);
    }

    public AnimationButton(Context context) {
        super(context, null);
    }

    public AnimationButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AnimationButton);
        int attrCount = attrs.getAttributeCount();

        for (int i = 0; i < attrCount; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
            case R.styleable.AnimationButton_icon_text:
                iconText = array.getString(attr);
                break;
            case R.styleable.AnimationButton_content_text:
                contentText = array.getString(attr);
                break;
            case R.styleable.AnimationButton_animation_text:
                animationText = array.getString(attr);
                break;
            default:
                break;
            }
        }
        array.recycle();

        resetChildView();
    }

    private void resetChildView() {
        mView = View.inflate(getContext(), R.layout.animation_button_layout, this);
        mView.setOnClickListener(this);

        ((IconView) mView.findViewById(R.id.anim_icon_view)).setText(iconText);
        mContentView = (TextView) mView.findViewById(R.id.anim_content_view);
        mContentView.setText(contentText);
        mAnimView = (TextView) mView.findViewById(R.id.anim_confirm_view);
        mAnimView.setVisibility(View.INVISIBLE);
        mAnimView.setText(animationText);

        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);

        mResultView = (TextView) mView.findViewById(R.id.anim_result_view);

        mHandler = new Handler(this);
    }

    public void showResult() {
        mContentView.setTextColor(Color.GRAY);
        mView.setEnabled(false);
        mResultView.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(0, 5000);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.anim_confirm_view) {

            if (onConfirmLisntener != null) {
                if (onConfirmLisntener.onConfirm(this)) {
                    showResult();
                }

            }
            mAnimView.setVisibility(View.INVISIBLE);
            mAnimView.setOnClickListener(null);
        } else {
            int visible = mAnimView.getVisibility();
            visible = visible == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
            mAnimView.setVisibility(visible);
            if (visible == View.VISIBLE) {
                mAnimView.startAnimation(mAnimation);
                mAnimView.setOnClickListener(this);
            } else {
                mAnimView.setOnClickListener(null);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        mResultView.setVisibility(View.GONE);
        mContentView.setTextColor(getResources().getColor(R.color.black));
        mView.setEnabled(true);
        return true;
    }

    public OnConfirmLisntener getOnConfirmLisntener() {
        return onConfirmLisntener;
    }

    public void setOnConfirmLisntener(OnConfirmLisntener onConfirmLisntener) {
        this.onConfirmLisntener = onConfirmLisntener;
    }

}
