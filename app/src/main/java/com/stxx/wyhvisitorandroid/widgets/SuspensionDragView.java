package com.stxx.wyhvisitorandroid.widgets;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

/**
 * description:可拖动的悬浮按钮
 * Created by liNan on 2017/12/21.
 */

public class SuspensionDragView implements View.OnTouchListener {
    private Builder mBuilder;

    private int mStatusBarHeight, mScreenWidth, mScreenHeight;

    //手指按下位置
    private int mStartX, mStartY, mLastX, mLastY;
    private boolean mTouchResult = false;

    private SuspensionDragView(Builder builder) {
        mBuilder = builder;
        initDragView();
    }

    public View getDragView() {
        return mBuilder.view;
    }

    public Activity getActivity() {
        return mBuilder.activity;
    }

    public boolean getNeedNearEdge() {
        return mBuilder.needNearEdge;
    }

    public void setNeedNearEdge(boolean needNearEdge) {
        mBuilder.needNearEdge = needNearEdge;
        if (mBuilder.needNearEdge) {
            moveNearEdge();
        }
    }

    private void initDragView() {
        if (null == getActivity()) {
            throw new NullPointerException("the activity is null");
        }
        if (null == mBuilder.view) {
            throw new NullPointerException("the dragView is null");
        }
        if (mBuilder.activity.isDestroyed()) {
            return;
        }

        //屏幕宽高
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }

        //状态栏高度
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight = getActivity().getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        int left = mBuilder.defaultLeft;
        FrameLayout.LayoutParams layoutParams = createLayoutParams(left, mStatusBarHeight + mBuilder.defaultTop, 0, 0);
        FrameLayout rootLayout = (FrameLayout) getActivity().getWindow().getDecorView();
        rootLayout.addView(getDragView(), layoutParams);
        getDragView().setOnTouchListener(this);
        getDragView().setVisibility(mBuilder.isVisible ? View.VISIBLE : View.GONE);
    }

    private static SuspensionDragView createDragView(Builder builder) {
        if (null == builder) {
            throw new NullPointerException("the param builder is null when execute method createDragView");
        }
        if (null == builder.activity) {
            throw new NullPointerException("the activity is null");
        }
        if (null == builder.view) {
            throw new NullPointerException("the view is null");
        }
        return new SuspensionDragView(builder);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchResult = false;
                mStartX = mLastX = (int) event.getRawX();
                mStartY = mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int left, top, right, bottom;
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                left = v.getLeft() + dx;
                if (left < 0) {
                    left = 0;
                }
                right = left + v.getWidth();
                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }
                top = v.getTop() + dy;
                if (top < mStatusBarHeight + 2) {
                    top = mStatusBarHeight + 2;
                }
                bottom = top + v.getHeight();
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (Math.abs(endX - mStartX) > 5 || Math.abs(endY - mStartY) > 5) {
                    //防止点击的时候稍微有点移动点击事件被拦截了
                    mTouchResult = true;
                }
                if (mTouchResult && mBuilder.needNearEdge) {
                    //是否每次都移至屏幕边沿
                    moveNearEdge();
                }
                break;
        }
        return mTouchResult;
    }

    /**
     * 移至最近的边沿
     */
    private void moveNearEdge() {
        int left = getDragView().getLeft();
        Logger.i(String.valueOf(left));
        int lastX;
        if (left + getDragView().getWidth() / 2 <= mScreenWidth / 2) {
            lastX = -dp2px(20);
        } else {
            lastX = mScreenWidth - getDragView().getWidth() + dp2px(20);
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left, lastX);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            int left1 = (int) animation.getAnimatedValue();
            getDragView().setLayoutParams(createLayoutParams(left1, getDragView().getTop(), 0, 0));
        });
        valueAnimator.start();
    }

    private int dp2px(int dp) {
        DisplayMetrics dls = Resources.getSystem().getDisplayMetrics();
        return (int) (dls.density * dp + 0.5f);

    }

    private FrameLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mBuilder.size, mBuilder.size);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static class Builder {
        private Activity activity;
        private int size = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int defaultTop = 0;
        private int defaultLeft = 0;
        private boolean needNearEdge = true;
        private View view;
        private boolean isVisible;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setDefaultTop(int top) {
            this.defaultTop = top;
            return this;
        }

        public Builder setDefaultLeft(int left) {
            this.defaultLeft = left;
            return this;
        }

        public Builder setNeedNearEdge(boolean needNearEdge) {
            this.needNearEdge = needNearEdge;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setVisible(boolean visible) {
            isVisible = visible;
            return this;
        }

        public SuspensionDragView build() {
            return createDragView(this);
        }
    }
}
