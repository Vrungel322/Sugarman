package com.sugarman.myb.api.models.responses.trackings;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;

public class TrackingsResponse {

  @SerializedName("result") private Tracking[] result;

  public Tracking[] getResult() {
    return result;
  }

  public void setResult(Tracking[] result) {
    this.result = result;
  }
}
