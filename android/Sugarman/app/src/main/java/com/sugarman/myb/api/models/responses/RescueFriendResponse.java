package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.me.groups.CreatedGroup;

/**
 * Created by yegoryeriomin on 1/19/18.
 */

public class RescueFriendResponse {

  @SerializedName("result") private String result;

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
