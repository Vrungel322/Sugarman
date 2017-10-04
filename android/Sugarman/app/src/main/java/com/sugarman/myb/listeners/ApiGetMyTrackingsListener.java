package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;

public interface ApiGetMyTrackingsListener extends ApiBaseListener {

  void onApiGetMyTrackingSuccess(Tracking[] trackings, boolean isRefreshNotification);

  void onApiGetMyTrackingsFailure(String message);
}
