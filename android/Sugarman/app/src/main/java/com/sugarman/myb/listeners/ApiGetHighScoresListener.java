package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.score.HighScores;

public interface ApiGetHighScoresListener extends ApiBaseListener {

  void onApiGetHighScoresSuccess(HighScores scores);

  void onApiGetHighScoresFailure(String message);
}
