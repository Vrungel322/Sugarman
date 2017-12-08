package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor public class PokeRequest {

  @Getter @Setter @SerializedName("user_id") private String userId;

  @Getter @Setter @SerializedName("tracking_id") private String trackingId;
}
