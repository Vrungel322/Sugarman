package com.sugarman.myb.ui.views;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.sugarman.myb.listeners.OnSwipeListener;

public class OnSwipeTouchListener implements View.OnTouchListener {

  private final GestureDetector gestureDetector;

  public OnSwipeTouchListener(Context context, OnSwipeListener swipeListener) {
    gestureDetector = new GestureDetector(context, new SwipeGestureListener(swipeListener));
  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    return gestureDetector.onTouchEvent(event);
  }
}
