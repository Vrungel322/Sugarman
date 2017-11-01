package com.sugarman.myb.api.models.responses.me.trackings;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.models.mentors_group.MentorsGroup;
import java.util.List;
import lombok.Getter;

public class MyTrackingsResponse {

  @SerializedName("result") private Tracking[] result;
  @Getter @SerializedName("result_mentors") private List<MentorsGroup> mentorsGroup;

  public Tracking[] getResult() {
    return result;
  }

  public void setResult(Tracking[] result) {
    this.result = result;
  }
}
