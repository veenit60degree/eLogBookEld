package com.constants;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class ZoomScrollViewwww extends ScrollView
{
    private View imageView;
    private ViewGroup.LayoutParams lp_move,lp_up;
    private int startY,offsetY;
    private int height,width;
    private boolean isFirst=true;
    private int MaxHeight=0,MaxWidth=0,MaxOffsetY=0;

    public ZoomScrollViewwww(Context context)
    {
        super(context);
    }
    public ZoomScrollViewwww(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public ZoomScrollViewwww(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        ViewGroup vg = (ViewGroup) getChildAt(0);
        imageView = vg.getChildAt(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                startY = (int) ev.getY();
                MaxHeight = 0;
                MaxWidth = 0;
                MaxOffsetY = 0;

                if (getScrollY() == 0 && isFirst == true)
                {
                    height = imageView.getHeight();
                    width = imageView.getWidth();
                    isFirst = false;
                }
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {
                offsetY = ((int) ev.getY()) - startY;

                lp_move = imageView.getLayoutParams();

                if (offsetY > 0 && getScrollY() == 0)
                {

                    lp_move.height = height + (int)(offsetY * 0.5);
                    lp_move.width = width + (int)(offsetY * 0.5);
                    imageView.setLayoutParams(lp_move);

                    MaxHeight = Math.max(imageView.getHeight(), MaxHeight);
                    MaxWidth = Math.max(imageView.getWidth(), MaxWidth);
                    MaxOffsetY = Math.max(offsetY, MaxOffsetY);
                }
                else if (imageView.getHeight() > height)
                {
                    scrollTo(0, 0);

                    lp_move.height = Math.max((MaxHeight - (MaxOffsetY - offsetY)), height);
                    lp_move.width = Math.max((MaxWidth - (MaxOffsetY - offsetY)), width);
                    imageView.setLayoutParams(lp_move);
                }
                else
                {
                    startY = (int) ev.getY();
                    MaxHeight = 0;
                    MaxWidth = 0;
                    MaxOffsetY = 0;
                }}
            break;
            case MotionEvent.ACTION_UP:
            {
                ValueAnimator anim=ValueAnimator.ofFloat(1.0f, 0.0f);
                anim.setDuration(400);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

                    @Override
                    public void onAnimationUpdate(ValueAnimator p1)
                    {
                        float current= (float) p1.getAnimatedValue();
                        lp_up = imageView.getLayoutParams();
                        lp_up.height = height + (int)((imageView.getHeight() - height) * current);
                        lp_up.width = width + (int)((imageView.getWidth() - width) * current);
                        imageView.setLayoutParams(lp_up);
                    }
                });
                anim.start();

            }
            break;

        }
        return super.dispatchTouchEvent(ev);

    }
}
