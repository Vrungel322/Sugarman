package com.sugarman.myb.api.models.responses.me.stats;

import com.google.gson.annotations.SerializedName;

public class StatsResponse {

  @SerializedName("result") private Stats[] result;

  public Stats[] getResult() {
    return result;
  }

  public void setResult(Stats[] result) {
    this.result = result;
  }
}
