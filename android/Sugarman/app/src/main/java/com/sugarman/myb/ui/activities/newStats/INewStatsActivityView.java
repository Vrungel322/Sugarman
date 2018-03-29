package com.sugarman.myb.ui.activities.newStats;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import java.util.List;

/**
 * Created by nikita on 15.03.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface INewStatsActivityView
    extends MvpView {
  void showStats(List<Stats> statsCached);

  void showTrackingStats(List<Stats> statsCached);

  void showDayStats(List<Stats> stats);

  void changeGraphData();
}
