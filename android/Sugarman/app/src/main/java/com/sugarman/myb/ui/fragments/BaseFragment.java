package com.sugarman.myb.ui.fragments;

import android.support.v4.app.FragmentActivity;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.sugarman.myb.utils.DeviceHelper;

public abstract class BaseFragment extends MvpAppCompatFragment {

  public boolean isFragmentHidden() {
    return !isAdded() || isDetached() || isRemoving();
  }

  public void closeFragment() {
    if (getActivity() != null) {
      FragmentActivity activity = getActivity();
      DeviceHelper.hideKeyboard(activity);
      activity.getSupportFragmentManager()
          .beginTransaction()
          .remove(this)
          .commitAllowingStateLoss();
    }
  }
}
