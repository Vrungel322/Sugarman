package com.sugarman.myb.api.models.responses.me.notifications;

import com.google.gson.annotations.SerializedName;

public class NotificationsResponse {

  @SerializedName("result") private Notification[] result;

  public Notification[] getResult() {
    return result;
  }

  public void setResult(Notification[] result) {
    this.result = result;
  }
}
