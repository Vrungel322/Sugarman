package com.sugarman.myb.api.models.responses.me;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;

public class EditGroupResponse {
  @SerializedName("result") private Tracking result;

  public Tracking getResult() {
    return result;
  }

  public void setResult(Tracking result) {
    this.result = result;
  }
}
