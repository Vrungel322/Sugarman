package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.groups.CreatedGroup;

public interface ApiCreateGroupListener extends ApiBaseListener {

  void onApiCreateGroupSuccess(CreatedGroup group);

  void onApiCreateGroupFailure(String message);
}
