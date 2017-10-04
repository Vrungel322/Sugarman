package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.requests.Request;

public interface OnRequestsActionListener {

  void onDeclineRequest(Request request, int position);

  void onApproveRequest(Request request, int position);
}
