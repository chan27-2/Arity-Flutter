// Copyright (C) 2010 Mihai Preda

package com.chan272.arity_widget;

import android.view.MotionEvent;

class MotionEventWrapNew {
    static int getPointerCount(MotionEvent event) {
        return event.getPointerCount();
    }

    static float getX(MotionEvent event, int idx) {
        return event.getX(idx);
    }

    static float getY(MotionEvent event, int idx) {
        return event.getY(idx);
    }
}
