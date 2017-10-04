package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class PokeRequest {

  @SerializedName("user_id") private String userId;

  @SerializedName("tracking_id") private String trackingId;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(String trackingId) {
    this.trackingId = trackingId;
  }
}
