package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.users.Result;
import com.sugarman.myb.api.models.responses.users.User;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class CountInvitesResponse {

  @SerializedName("count") private String count;

  public String getCount() {
    return count;
  }
}
