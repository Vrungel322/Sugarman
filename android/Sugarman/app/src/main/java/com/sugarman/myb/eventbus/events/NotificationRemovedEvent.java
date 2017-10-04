package com.sugarman.myb.eventbus.events;

public class NotificationRemovedEvent {

  private final String notificationId;

  public NotificationRemovedEvent(String notificationId) {
    this.notificationId = notificationId;
  }

  public String getNotificationId() {
    return notificationId;
  }
}
