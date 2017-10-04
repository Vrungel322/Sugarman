package com.sugarman.myb.api.models.responses.trackings;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.me.stats.Stats;

public class TrackingStatsResponse {

  @SerializedName("result") private Stats[] result;

  public Stats[] getResult() {
    return result;
  }

  public void setResult(Stats[] result) {
    this.result = result;
  }
}
