package com.sugarman.myb.api.models.responses.me.score;

import com.google.gson.annotations.SerializedName;

public class HighScoreResponse {

  @SerializedName("result") private HighScores result;

  public HighScores getResult() {
    return result;
  }

  public void setResult(HighScores result) {
    this.result = result;
  }
}
