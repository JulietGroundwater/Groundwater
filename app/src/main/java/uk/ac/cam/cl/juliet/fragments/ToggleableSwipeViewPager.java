package uk.ac.cam.cl.juliet.fragments;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/** Offers the same functionality as a ViewPager, except you can disable sideways swiping. */
public class ToggleableSwipeViewPager extends ViewPager {

    private boolean allowSwiping = true;

    public ToggleableSwipeViewPager(Context context) {
        super(context);
    }

    public ToggleableSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAllowSwiping(boolean swipingAllowed) {
        allowSwiping = swipingAllowed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return allowSwiping && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return allowSwiping && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return allowSwiping && super.canScrollHorizontally(direction);
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        return allowSwiping && super.executeKeyEvent(event);
    }
}
