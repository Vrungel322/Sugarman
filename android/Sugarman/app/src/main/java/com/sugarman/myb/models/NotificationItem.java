package com.sugarman.myb.models;

import android.os.Parcel;
import android.text.SpannableStringBuilder;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;

public class NotificationItem extends Notification {

  private int type = -1;

  private SpannableStringBuilder span;

  protected NotificationItem(Parcel in) {
    super(in);
  }

  public NotificationItem(Notification notification) {
    setCreatedAt(notification.getCreatedAt());
    setId(notification.getId());
    setOriginator(notification.getUser());
    setRecipient(notification.getRecipient());
    setText(notification.getText());
    setUpdatedAt(notification.getUpdatedAt());
    setUser(notification.getUser());
    setTrackingId(notification.getTrackingId());
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public SpannableStringBuilder getSpan() {
    return span;
  }

  public void setSpan(SpannableStringBuilder span) {
    this.span = span;
  }
}
