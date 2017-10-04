package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;

public class FacebookInvitableResponse {

  @SerializedName("data") private FacebookFriend[] data;

  public FacebookFriend[] getData() {
    return data;
  }

  public void setData(FacebookFriend[] data) {
    this.data = data;
  }
}
