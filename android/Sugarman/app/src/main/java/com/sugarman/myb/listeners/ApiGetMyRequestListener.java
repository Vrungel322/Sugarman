package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.requests.Request;

public interface ApiGetMyRequestListener extends ApiBaseListener {

  void onApiGetMyRequestsSuccess(Request[] requests, boolean isRefreshTrackings);

  void onApiGetMyRequestsFailure(String message);
}
