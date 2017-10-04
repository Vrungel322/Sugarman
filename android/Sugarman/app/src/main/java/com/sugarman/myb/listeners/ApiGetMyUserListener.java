package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.users.User;

public interface ApiGetMyUserListener extends ApiBaseListener {

  void onApiGetMyUserSuccess(User user);

  void onApiGetMyUserFailure(String message);
}
