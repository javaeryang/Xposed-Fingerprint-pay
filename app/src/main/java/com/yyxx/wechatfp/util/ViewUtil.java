package com.yyxx.wechatfp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.yyxx.wechatfp.util.log.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jason on 2017/9/9.
 */

public class ViewUtil {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    public static int initId(View view) {
        int id;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = generateViewId();
        } else {
            id = View.generateViewId();
        }
        view.setId(id);
        return id;
    }

    public static void performActionClick(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 200;

        float x = width > 0 ? new Random(downTime).nextInt(width) : 0;
        float y = height > 0 ? new Random(eventTime).nextInt(height) : 0;

        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                metaState
        );
        view.dispatchTouchEvent(motionEvent);
        downTime = SystemClock.uptimeMillis() + 120;
        eventTime = SystemClock.uptimeMillis() + 200;
        motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );
        view.dispatchTouchEvent(motionEvent);
    }

    public static Drawable genBackgroundDefaultDrawable() {
        return genBackgroundDefaultDrawable(Color.TRANSPARENT);
    }

    public static Drawable genBackgroundDefaultDrawable(int defaultColor) {
        StateListDrawable statesDrawable = new StateListDrawable();
        statesDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(0xFFE5E5E5));
        statesDrawable.addState(new int[]{}, new ColorDrawable(defaultColor));
        return statesDrawable;
    }

    @Nullable
    public static View findViewByName(Activity activity, String packageName, String... names) {
        Resources resources = activity.getResources();
        for (String name : names) {
            int id = resources.getIdentifier(name, "id", packageName);
            if (id == 0) {
                continue;
            }
            View rootView = activity.getWindow().getDecorView();
            List<View> viewList = new ArrayList<>();
            getChildViews((ViewGroup) rootView, id, viewList);
            int outViewListSize = viewList.size();
            if (outViewListSize == 1) {
                return viewList.get(0);
            } else if (outViewListSize > 1) {
                for (View view : viewList) {
                    if (view.isShown()) {
                        return view;
                    }
                }
                return viewList.get(0);
            }
        }
        return null;
    }


    public static void recursiveLoopChildren(ViewGroup parent) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                recursiveLoopChildren((ViewGroup) child);
                // DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED
                L.d("ViewGroup", child);
            } else {
                if (child != null) {
                    try {
                        L.d("view", child);
                    } catch (Exception e) {

                    }
                    // DO SOMETHING WITH VIEW
                }
            }
        }
    }

    public static void getChildViews(ViewGroup parent, int id,List<View> outList) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }
            if (id == child.getId()) {
                outList.add(child);
            }
            if (child instanceof ViewGroup) {
                getChildViews((ViewGroup) child, id, outList);
            } else {
            }
        }
    }
}
