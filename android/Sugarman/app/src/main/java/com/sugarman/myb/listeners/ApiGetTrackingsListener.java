package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;

public interface ApiGetTrackingsListener extends ApiBaseListener {

  void onApiGetTrackingsSuccess(String type, Tracking[] trackings);

  void onApiGetTrackingsFailure(String type, String message);
}
