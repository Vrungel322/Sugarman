package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.stats.Stats;

public interface ApiGetTrackingStatsListener extends ApiBaseListener {

  void onApiGetTrackingStatsSuccess(Stats[] stats);

  void onApiGetTrackingStatsFailure(String message);
}
