package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;

public interface ApiGetTrackingInfoListener extends ApiBaseListener {

  void onApiGetTrackingInfoSuccess(Tracking tracking);

  void onApiGetTrackingInfoFailure(String message);
}
