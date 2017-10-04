package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.notifications.Notification;

public interface ApiGetNotificationsListener extends ApiBaseListener {

  void onApiGetNotificationsSuccess(Notification[] notifications);

  void onApiGetNotificationsFailure(String message);
}
