package com.sugarman.myb.api.models.responses.trackings;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class TrackingInfoResponse {

  @SerializedName("result") private Tracking result;

  @Getter @Setter @SerializedName("comments") List<MentorsCommentsEntity> mentorsCommentsEntity;

  public Tracking getResult() {
    return result;
  }

  public void setResult(Tracking result) {
    this.result = result;
  }
}
