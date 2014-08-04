package com.ctrip.protoshop.mini.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.PopupWindow;
import com.ctrip.protoshop.mini.R;
import com.ctrip.protoshop.mini.constants.LinkViewState;
import com.ctrip.protoshop.mini.model.LinkModel;
import com.ctrip.protoshop.mini.util.ProtoshopLog;

@SuppressLint("ViewConstructor")
@SuppressWarnings("deprecation")
public class LinkView extends View {
    private static int RADIUS = 30;
    private static int DEFAUT_WIDTH = 100;
    private static int DEFAUT_HEIGH = 100;

    private NinePatchDrawable mUnlinkSelectDrawable;
    private NinePatchDrawable mUnLinkNormalDrawable;
    private NinePatchDrawable mLinkNormalDrawable;
    private NinePatchDrawable mLinkSelectDrawable;

    private Rect ltRect = new Rect();
    private Rect rtRect = new Rect();
    private Rect lbRect = new Rect();
    private Rect rbRect = new Rect();

    private PopupWindow mLinkPopupWindow;
    private int mPopupWindowHeight;
    private View mDeleteView;
    private View mLinkView;

    private LinkModel mLinkModel;
    private LinkViewState mState=LinkViewState.UNLINK_SELECT;

    private OnLinkListener onLinkListener;

    public interface OnLinkListener {
        public void onLink(LinkModel linkModel);

        public void onCancel(LinkModel linkModel);
    }

    public LinkView(Context context, LinkModel linkModel) {
        super(context);

        mLinkModel = linkModel;

        initLinkView(context);
        initPopupWindow(context);
    }

    private void initPopupWindow(Context context) {
        mPopupWindowHeight = getResources().getDimensionPixelSize(R.dimen.pop_height);

        View contentView = inflate(context, R.layout.link_view_layout, null);
        mDeleteView = contentView.findViewById(R.id.link_delete_view);
        mLinkView = contentView.findViewById(R.id.link_link_view);

        mLinkPopupWindow = new PopupWindow(context);
        mLinkPopupWindow.setContentView(contentView);
        mLinkPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinkPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinkPopupWindow.setBackgroundDrawable(null);

        addOnListener();
    }

    private void addOnListener() {
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (mState == LinkViewState.LINK_SELECT || mState == LinkViewState.UNLINK_SELECT) {
                    showPopuWindow(LinkView.this);
                } else {
                    mLinkPopupWindow.dismiss();
                }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ProtoshopLog.e("into-[onFocusChange]:" + hasFocus);
                    showPopuWindow(LinkView.this);

                    if (mState == LinkViewState.LINK_NORMAL) {
                        mState = LinkViewState.LINK_SELECT;
                        setBackgroundDrawable(mLinkSelectDrawable);
                    } else if (mState == LinkViewState.UNLINK_NORMAL) {
                        mState = LinkViewState.UNLINK_SELECT;
                        setBackgroundDrawable(mUnlinkSelectDrawable);
                    }

                } else {
                    ProtoshopLog.e("into-[onFocusChange]:" + hasFocus);
                    mLinkPopupWindow.dismiss();
                    if (mState == LinkViewState.LINK_SELECT) {
                        mState = LinkViewState.LINK_NORMAL;
                        setBackgroundDrawable(mLinkNormalDrawable);
                    } else if (mState == LinkViewState.UNLINK_SELECT) {
                        mState = LinkViewState.UNLINK_NORMAL;
                        setBackgroundDrawable(mUnLinkNormalDrawable);
                    }

                }
            }
        });

        mDeleteView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onLinkListener.onCancel(mLinkModel);
                ViewGroup group = (ViewGroup) getParent();
                if (group != null) {
                    mLinkPopupWindow.dismiss();
                    group.removeView(LinkView.this);
                }
            }
        });

        mLinkView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onLinkListener.onLink(mLinkModel);
            }
        });
    }

    private void initLinkView(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hotspots_red_select);
        mUnlinkSelectDrawable = new NinePatchDrawable(getResources(), bitmap, bitmap.getNinePatchChunk(), new Rect(0,
            0, 0, 0), "UnLinkSelect");

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hotspots_red_unselect);
        mUnLinkNormalDrawable = new NinePatchDrawable(getResources(), bitmap, bitmap.getNinePatchChunk(), new Rect(0,
            0, 0, 0), "LinkNormal");

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hotspots_blue_unselect);
        mLinkNormalDrawable = new NinePatchDrawable(getResources(), bitmap, bitmap.getNinePatchChunk(), new Rect(0, 0,
            0, 0), "LinkNormal");

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hotspots_blue_select);
        mLinkSelectDrawable = new NinePatchDrawable(getResources(), bitmap, bitmap.getNinePatchChunk(), new Rect(0, 0,
            0, 0), "LinkNormal");

        AbsoluteLayout.LayoutParams params = new LayoutParams(DEFAUT_WIDTH, DEFAUT_HEIGH, 100, 200);
        setLayoutParams(params);
        setBackgroundDrawable(mUnlinkSelectDrawable);
    }

    private void resetRect(Rect rect, int centerX, int centerY) {
        int left = centerX - RADIUS;
        int top = centerY - RADIUS;
        int right = centerX + RADIUS;
        int bottom = centerY + RADIUS;

        rect.set(left, top, right, bottom);
    }

    private void showPopuWindow(View anchor) {
        int[] point = new int[2];
        getLocationOnScreen(point);
        mLinkPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, point[0], point[1] - mPopupWindowHeight);
    }

    public void updateLinkViewState(LinkViewState state) {
        mState = state;
        switch (state) {
        case UNLINK_NORMAL:
            mLinkPopupWindow.dismiss();
            setBackgroundDrawable(mUnLinkNormalDrawable);
            break;
        case UNLINK_SELECT:
            showPopuWindow(this);
            setBackgroundDrawable(mUnlinkSelectDrawable);
            break;
        case LINK_NORMAL:
            mLinkPopupWindow.dismiss();
            setBackgroundDrawable(mLinkNormalDrawable);
            break;
        case LINK_SELECT:
            showPopuWindow(this);
            setBackgroundDrawable(mLinkSelectDrawable);
            break;

        default:
            break;
        }
    }

    private void updateRects() {
        Rect rect = new Rect();
        getLocalVisibleRect(rect);

        resetRect(ltRect, rect.left, rect.top);
        resetRect(rtRect, rect.right, rect.top);
        resetRect(rbRect, rect.right, rect.bottom);
        resetRect(lbRect, rect.left, rect.bottom);
    }

    int lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        setFocusableInTouchMode(true);
        requestFocusFromTouch();

        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mLinkPopupWindow.dismiss();

            lastX = (int) event.getX();
            lastY = (int) event.getY();
            return true;
        case MotionEvent.ACTION_MOVE:

            updateRects();

            int pointX = (int) event.getX();
            int pointY = (int) event.getY();

            int dx = (int) (pointX - lastX);
            int dy = (int) (pointY - lastY);

            AbsoluteLayout.LayoutParams params = (LayoutParams) getLayoutParams();
            if (params == null) {
                params = new LayoutParams(DEFAUT_WIDTH, DEFAUT_HEIGH, 0, 0);
            }

            if (ltRect.contains(pointX, pointY)) {
                params.x += dx;
                params.y += dy;
                params.width -= dx;
                params.height -= dy;
            } else if (rbRect.contains(pointX, pointY)) {

                lastX = pointX;
                lastY = pointY;

                params.width += dx;
                params.height += dy;
            } else if (lbRect.contains(pointX, pointY)) {
                lastY = pointY;

                params.x += dx;
                params.width -= dx;
                params.height += dy;

            } else if (rtRect.contains(pointX, pointY)) {
                lastX = pointX;

                params.y += dy;
                params.width += dx;
                params.height -= dy;
            } else {
                params.x += dx;
                params.y += dy;
            }

            setLayoutParams(params);
            requestLayout();
            return true;
        case MotionEvent.ACTION_UP:
            showPopuWindow(this);
            params = (LayoutParams) getLayoutParams();
            mLinkModel.height = String.valueOf(params.height);
            mLinkModel.width = String.valueOf(params.width);
            mLinkModel.posX = String.valueOf(params.x);
            mLinkModel.posY = String.valueOf(params.y);
            return true;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    public OnLinkListener getOnLinkListener() {
        return onLinkListener;
    }

    public void setOnLinkListener(OnLinkListener onLinkListener) {
        this.onLinkListener = onLinkListener;
    }

    public LinkModel getLinkModel() {
        return mLinkModel;
    }

    public void setLinkModel(LinkModel mLinkModel) {
        this.mLinkModel = mLinkModel;
    }

}
