package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class StatsRequest {

  @SerializedName("data") private ReportStats[] data;

  public ReportStats[] getData() {
    return data;
  }

  public void setData(ReportStats[] data) {
    this.data = data;
  }
}
