package com.sugarman.myb.api.models.responses.me.score;

import com.google.gson.annotations.SerializedName;

public class HighScores {

  @SerializedName("created") private HighScore[] created;

  @SerializedName("participated") private HighScore[] participated;

  public HighScore[] getCreated() {
    return created;
  }

  public void setCreated(HighScore[] created) {
    this.created = created;
  }

  public HighScore[] getParticipated() {
    return participated;
  }

  public void setParticipated(HighScore[] participated) {
    this.participated = participated;
  }
}
