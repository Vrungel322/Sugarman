package com.sugarman.myb.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sugarman.myb.R;

public class ProgressFragment extends BaseFragment implements View.OnClickListener {

  private static final String TAG = ProgressFragment.class.getName();

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_progress, container, false);

    View mainContainer = root.findViewById(R.id.ll_main_container);

    mainContainer.setOnClickListener(this);

    return root;
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.ll_main_container:
        // nothing
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }
}