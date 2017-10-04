package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;

public class FriendsSummary {

  @SerializedName("total_count") private int totalCount;

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
}
