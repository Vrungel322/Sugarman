package com.sugarman.myb.listeners;

import com.sugarman.myb.models.NotificationItem;

public interface OnNotificationActionListener {

  void onNotificationClick(int position, NotificationItem item);

  void onNotificationRemove(int position, NotificationItem item);

  void onNotificationRemoved();
}
