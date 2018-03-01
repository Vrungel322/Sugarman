package com.sugarman.myb.ui.activities.statsTracking;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import java.util.List;

/**
 * Created by nikita on 01.03.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IStatsTrackingActivityView
    extends MvpView {
  void showTrackingStats(List<Stats> stats);
}
