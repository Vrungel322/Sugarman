package com.sugarman.myb.ui.activities.myStats;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;

/**
 * Created by yegoryeriomin on 2/28/18.
 */

@StateStrategyType(AddToEndSingleStrategy.class) public interface IMyStatsActivityView extends
    MvpView {

  void showStats(StatsResponse stats);
}
