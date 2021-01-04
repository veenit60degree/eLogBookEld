/*
 * Copyright (C) 2018 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.drag.slide.listview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


class DragManager implements Callback.OnDragDropListener {
    /* drag Transparency */
    private static final float DRAG_VIEW_ALPHA = 0.7f;

    /* drag of View */
    private ImageView mDragView;
    /* Inner View */
    private DragListView mDragListView;
    /* Handler Delay */
    private final long SCROLL_HANDLER_DELAY_MILLIS = 5;
    /* Moving distance */
    private final int DRAG_SCROLL_PX_UNIT = 25;
    /* Handler */
    private Handler mScrollHandler;
    /* The boundary ratio, the position to this ratio begins to move */
    private final float BOUND_GAP_RATIO = 0.2f;
    /* boundary */
    private int mTopScrollBound;
    private int mBottomScrollBound;
    /* Y-axis coordinate when pressed */
    private int mTouchDownForDragStartY;
    /* move Y-axis coordinate */
    private int mLastDragY;
    /* Did you enter the scroll handler */
    private boolean mIsDragScrollerRunning = false;
    /* shortest distance     */
    private float mTouchSlop;
    /* drag View of the Bitmap */
    private Bitmap mDragViewBitmap;
    /* drag Error */
    private int mDragDelta;
    /* Is it being drag */
    private boolean isDragging;
    /* Has it been recalled handleDragStarted */
    private boolean isInvokedDraggingStarted;
    /* Finger X and Y*/
    private float mFingerX, mFingerY;
    /* decorView */
    private ViewGroup mDecorView;
    /* The difference between the current View and the decorView */
    private int[] mLeftAndTopOffset;
    /* Dragged view */
    private ItemMainLayout mItemMainLayout;

    public DragManager(Context context, DragListView dragListView, ViewGroup decorView) {
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mDragListView = dragListView;
        mDragListView.setListDragDropListener(this);
        mDragView = new ImageView(context);
        mDecorView = decorView;
        mDecorView.addView(mDragView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        mLeftAndTopOffset = new int[]{0, 0};
    }

    protected void onSizeChanged() {
        mLeftAndTopOffset = new int[]{0, 0};
        getOffset(mDragListView, mDecorView, mLeftAndTopOffset);
    }

    private void getOffset(View current, View decorView, int[] array) {
        if (current != decorView) {
            getOffset(current, array);
            if (current.getParent() != null) {
                View view = (View) current.getParent();
                getOffset(view, decorView, array);
            }
        }
    }

    private void getOffset(View view, int[] array) {
        array[0] = array[0] + view.getLeft() + view.getPaddingLeft();
        array[1] = array[1] + view.getTop() + view.getPaddingLeft();
    }

    protected void setDragging(boolean dragging) {
        this.isDragging = dragging;
        invokedDraggingStarted();
    }

    protected boolean isDragging() {
        return isDragging;
    }

    private void invokedDraggingStarted() {
        if (!isInvokedDraggingStarted && isDragging) {
            mDragListView.handleDragStarted((int) mFingerX, (int) mFingerY);
            isInvokedDraggingStarted = true;
            calculateBound();
        }
    }

    private void calculateBound() {
        final int boundGap = (int) (mDragListView.getHeight() * BOUND_GAP_RATIO);
        mTopScrollBound = (mDragListView.getTop() + boundGap);
        mBottomScrollBound = (mDragListView.getBottom() - boundGap);
    }

    protected boolean onInterceptTouchEvent(MotionEvent ev) {
        mFingerX = ev.getX();
        mFingerY = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchDownForDragStartY = (int) ev.getY();
        }
        invokedDraggingStarted();
        return isDragging;
    }

    protected boolean onTouchEvent(MotionEvent event) {
        mFingerX = event.getX();
        mFingerY = event.getY();
        if (!isDragging) {
            return false;
        }
        int eX = (int) event.getX();
        int eY = (int) event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                invokedDraggingStarted();
                break;
            case MotionEvent.ACTION_MOVE:
                invokedDraggingStarted();
                mLastDragY = eY;
                mDragListView.handleDragMoving(eX, eY);
                if (!mIsDragScrollerRunning && (Math.abs(mLastDragY - mTouchDownForDragStartY) >= 4 * mTouchSlop)) {
                    mIsDragScrollerRunning = true;
                    ensureScrollHandler();
                    mScrollHandler.postDelayed(mDragScroller, SCROLL_HANDLER_DELAY_MILLIS);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ensureScrollHandler();
                mScrollHandler.removeCallbacks(mDragScroller);
                mIsDragScrollerRunning = false;
                mDragListView.handleDragFinished(eX, eY);
                isDragging = false;
                isInvokedDraggingStarted = false;
                break;
        }
        return isDragging;
    }

    private void ensureScrollHandler() {
        if (mScrollHandler == null) {
            mScrollHandler = new Handler();
        }
    }

    private final Runnable mDragScroller = new Runnable() {
        @Override
        public void run() {
            if (mLastDragY <= mTopScrollBound) {
                mDragListView.smoothScrollBy(-DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            } else if (mLastDragY >= mBottomScrollBound) {
                mDragListView.smoothScrollBy(DRAG_SCROLL_PX_UNIT, (int) SCROLL_HANDLER_DELAY_MILLIS);
            }
            mScrollHandler.postDelayed(this, SCROLL_HANDLER_DELAY_MILLIS);
        }
    };

    @Override
    public boolean onDragStarted(int x, int y, View view) {
        mDragViewBitmap = createDraggedChildBitmap(view);
        if (mDragViewBitmap == null) {
            return false;
        }
        if (view instanceof ItemMainLayout) {
            mItemMainLayout = (ItemMainLayout) view;
            mItemMainLayout.disableBackgroundDrawable();
        }
        mDragView.setImageBitmap(mDragViewBitmap);
        mDragView.setVisibility(View.VISIBLE);
        mDragView.setAlpha(DRAG_VIEW_ALPHA);
        mDragView.setX(mDragListView.getPaddingLeft());
        mDragDelta = y - view.getTop();
        mDragView.setY(y - mDragDelta + mLeftAndTopOffset[1]);
        return true;
    }

    private Bitmap createDraggedChildBitmap(View view) {
        if (view instanceof ItemMainLayout) {
            ((ItemMainLayout) view).disableBackgroundDrawable();
        }
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        Bitmap bitmap = null;
        if (cache != null) {
            try {
                bitmap = cache.copy(Bitmap.Config.ARGB_8888, false);
            } catch (OutOfMemoryError e) {
                bitmap = null;
            }
        }
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        if (view instanceof ItemMainLayout) {
            ((ItemMainLayout) view).enableBackgroundDrawable();
        }
        return bitmap;
    }

    @Override
    public void onDragMoving(int x, int y, View view, SlideAndDragListView.OnDragDropListener listener) {
        mDragView.setX(mLeftAndTopOffset[0]);
        mDragView.setY(y - mDragDelta + mLeftAndTopOffset[1]);
    }

    @Override
    public void onDragFinished(int x, int y, SlideAndDragListView.OnDragDropListener listener) {
        if (mDragView != null && mDragView.getVisibility() == View.VISIBLE) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDragView, "alpha", DRAG_VIEW_ALPHA, 0.0f);
            objectAnimator.setDuration(100);
            objectAnimator.addListener(new DragFinishAnimation());
            objectAnimator.start();
        }
    }

    private class DragFinishAnimation extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (mDragViewBitmap != null) {
                mDragViewBitmap.recycle();
                mDragViewBitmap = null;
            }
            mDragView.setVisibility(View.GONE);
            mDragView.setImageBitmap(null);
            if (mItemMainLayout != null) {
                mItemMainLayout.enableBackgroundDrawable();
                mItemMainLayout = null;
            }
        }
    }
}
