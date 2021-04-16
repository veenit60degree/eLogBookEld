package com.constants;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    private View mZoomView;
    private int mZoomViewWidth;
    private int mZoomViewHeight;

    private float firstPosition;//Record the position of the first press
    private boolean isScrolling;//Is zooming
    private float mScrollRate = 0.3f;//Zoom factor, the larger the zoom factor, the greater the change
    private float mReplyRate = 0.5f;//Callback coefficient, the larger the callback, the slower the callback

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setmZoomView(View mZoomView) {
        this.mZoomView = mZoomView;
    }

    public void setmScrollRate(float mScrollRate) {
        this.mScrollRate = mScrollRate;
    }

    public void setmReplyRate(float mReplyRate) {
        this.mReplyRate = mReplyRate;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        if (getChildAt(0) != null) {
            ViewGroup vg = (ViewGroup) getChildAt(0);
            if (vg.getChildAt(0) != null) {
                mZoomView = vg.getChildAt(0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mZoomViewWidth <= 0 || mZoomViewHeight <= 0) {
            mZoomViewWidth = mZoomView.getMeasuredWidth();
            mZoomViewHeight = mZoomView.getMeasuredHeight();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                //Restore the picture after leaving the finger
                isScrolling = false;
                replyImage();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScrolling) {
                    if (getScrollY() == 0) {
                        firstPosition = ev.getY();// Record position when scrolling to the top, otherwise return normally
                    } else {
                        break;
                    }
                }
                int distance = (int) ((ev.getY() - firstPosition) * mScrollRate); // Multiply the scroll distance by a factor
                if (distance < 0) { // The current position is smaller than the recorded position, return normally
                    break;
                }

                // Processing zoom
                isScrolling = true;
                setZoom(distance);
                return true; // Return true to indicate that the touch event has been completed and will no longer be processed
        }
        return true;
    }

    //Rebound animation
    private void replyImage() {
        float distance = mZoomView.getMeasuredWidth() - mZoomViewWidth;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distance, 0f).setDuration((long) (distance * mReplyRate));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setZoom((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    public void setZoom(float zoom) {
        if (mZoomViewWidth <= 0 || mZoomViewHeight <= 0) {
            return;
        }
        ViewGroup.LayoutParams lp = mZoomView.getLayoutParams();
        lp.width = (int) (mZoomViewWidth + zoom);
        lp.height = (int) (mZoomViewHeight * ((mZoomViewWidth + zoom) / mZoomViewWidth));
        ((MarginLayoutParams) lp).setMargins(-(lp.width - mZoomViewWidth) / 2, 0, 0, 0);
        mZoomView.setLayoutParams(lp);
    }

}
