package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;

public interface ApiJoinGroupListener extends ApiBaseListener {

  void onApiJoinGroupSuccess(Tracking result);

  void onApiJoinGroupFailure(String message);
}
