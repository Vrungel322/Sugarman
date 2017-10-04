package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;

public class FacebookFriendResponse {

  @SerializedName("data") private FacebookFriend[] data;

  @SerializedName("summary") private FriendsSummary summary;

  public FacebookFriend[] getData() {
    return data;
  }

  public void setData(FacebookFriend[] data) {
    this.data = data;
  }

  public FriendsSummary getSummary() {
    return summary;
  }

  public void setSummary(FriendsSummary summary) {
    this.summary = summary;
  }
}
