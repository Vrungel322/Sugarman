package com.sugarman.myb.api.models.responses.me.groups;

import com.google.gson.annotations.SerializedName;

public class CreateGroupResponse {

  @SerializedName("result") private CreatedGroup result;

  public CreatedGroup getResult() {
    return result;
  }

  public void setResult(CreatedGroup result) {
    this.result = result;
  }
}
