package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;

public class EditGroup {

  @SerializedName("tracking") private Tracking tracking;

  public Tracking getTracking() {
    return tracking;
  }

  public void setTracking(Tracking tracking) {
    this.tracking = tracking;
  }
}
