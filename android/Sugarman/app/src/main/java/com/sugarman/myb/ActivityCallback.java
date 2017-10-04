package com.sugarman.myb;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import java.lang.ref.WeakReference;

class ActivityCallback implements Application.ActivityLifecycleCallbacks {

  private WeakReference<Activity> mCurrent = new WeakReference<>(null);

  private boolean isAppForeground;

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    // nothing
  }

  @Override public void onActivityStarted(Activity activity) {
    mCurrent = new WeakReference<>(activity);

    isAppForeground = false;
  }

  @Override public void onActivityResumed(Activity activity) {
    // nothing
  }

  @Override public void onActivityPaused(Activity activity) {
    // nothing
  }

  @Override public void onActivityStopped(Activity activity) {
    if (mCurrent.get() == activity) {
      isAppForeground = true;
    }
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    // nothing
  }

  @Override public void onActivityDestroyed(Activity activity) {
    // nothing
  }

  Activity getCurrentActivity() {
    return mCurrent.get();
  }

  boolean isAppForeground() {
    return isAppForeground;
  }
}