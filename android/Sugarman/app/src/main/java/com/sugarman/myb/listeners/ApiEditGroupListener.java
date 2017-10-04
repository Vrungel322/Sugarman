package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;

public interface ApiEditGroupListener {
  void onApiEditGroupSuccess(Tracking group);

  void onApiEditGroupFailure(String message);
}
