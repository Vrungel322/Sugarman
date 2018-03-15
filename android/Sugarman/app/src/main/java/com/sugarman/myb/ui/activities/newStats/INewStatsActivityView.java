package com.sugarman.myb.ui.activities.newStats;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 15.03.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface INewStatsActivityView
    extends MvpView {
}
