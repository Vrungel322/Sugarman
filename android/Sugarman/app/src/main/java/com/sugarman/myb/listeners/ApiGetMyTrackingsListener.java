package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;
import java.util.List;

public interface ApiGetMyTrackingsListener extends ApiBaseListener {

  void onApiGetMyTrackingSuccess(Tracking[] trackings, List<Tracking> mentorsGroup, boolean isRefreshNotification);

  void onApiGetMyTrackingsFailure(String message);
}
