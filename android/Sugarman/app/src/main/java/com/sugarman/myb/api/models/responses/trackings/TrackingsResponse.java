package com.sugarman.myb.api.models.responses.trackings;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class TrackingsResponse {

  @Getter @Setter @SerializedName("result") private List<Tracking> result;
}
