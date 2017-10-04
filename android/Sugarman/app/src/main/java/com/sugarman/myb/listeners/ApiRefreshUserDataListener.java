package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.users.UsersResponse;

public interface ApiRefreshUserDataListener extends ApiBaseListener {

  void onApiRefreshUserDataSuccess(UsersResponse response);

  void onApiRefreshUserDataFailure(String message);
}
