package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.stats.Stats;

public interface ApiGetMyStatsListener extends ApiBaseListener {

  void onApiGetMyStatsSuccess(Stats[] stats);

  void onApiGetMyStatsFailure(String message);
}
