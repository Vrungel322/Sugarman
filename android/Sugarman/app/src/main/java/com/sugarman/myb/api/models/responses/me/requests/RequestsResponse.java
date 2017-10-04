package com.sugarman.myb.api.models.responses.me.requests;

import com.google.gson.annotations.SerializedName;

public class RequestsResponse {

  @SerializedName("result") private Request[] result;

  public Request[] getResult() {
    return result;
  }

  public void setResult(Request[] result) {
    this.result = result;
  }
}
