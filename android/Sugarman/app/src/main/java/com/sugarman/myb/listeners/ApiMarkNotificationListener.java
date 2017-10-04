package com.sugarman.myb.listeners;

public interface ApiMarkNotificationListener extends ApiBaseListener {

  void onApiMarkNotificationSuccess(String notificationId);

  void onApiMarkNotificationFailure(String notificationId, String message);
}
