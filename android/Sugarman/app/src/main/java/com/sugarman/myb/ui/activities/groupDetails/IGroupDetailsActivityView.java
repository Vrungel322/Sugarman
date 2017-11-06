package com.sugarman.myb.ui.activities.groupDetails;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 03.11.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IGroupDetailsActivityView
    extends MvpView {
  void closeDialog();

  void removeUser();
}
