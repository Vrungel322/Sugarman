package com.sugarman.myb.api.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor @ToString
public class StatsRequest {
  @Getter @Setter @SerializedName("start_date") private String startDate;
  @Getter @Setter @SerializedName("end_date") private String endDate;
}
