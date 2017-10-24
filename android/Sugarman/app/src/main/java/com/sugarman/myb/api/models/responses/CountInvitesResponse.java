package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class CountInvitesResponse {

  @SerializedName("count") private String count;

  public String getCount() {
    return count;
  }
}
