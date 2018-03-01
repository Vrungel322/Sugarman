package com.sugarman.myb.api.models.responses.me.stats;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor public class StatsResponse {

  @Getter @Setter @SerializedName("result") private Stats[] result;
}
