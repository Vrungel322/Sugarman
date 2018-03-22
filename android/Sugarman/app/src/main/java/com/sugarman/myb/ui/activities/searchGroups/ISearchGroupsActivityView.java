package com.sugarman.myb.ui.activities.searchGroups;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.Tracking;

/**
 * Created by nikita on 22.03.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface ISearchGroupsActivityView
    extends MvpView {
  void showTrackings(Tracking[] result, String availableType);
}
