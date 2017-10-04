package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class SendFirebaseTokenRequest {

  @SerializedName("token") private String firebaseToken;

  @SerializedName("platform") private String platform;

  @SerializedName("is_development") private boolean isDevelopment;

  @SerializedName("timezone") private String timezone;

  public String getFirebaseToken() {
    return firebaseToken;
  }

  public void setFirebaseToken(String firebaseToken) {
    this.firebaseToken = firebaseToken;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public boolean isDevelopment() {
    return isDevelopment;
  }

  public void setDevelopment(boolean development) {
    isDevelopment = development;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}
