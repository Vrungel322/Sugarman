package com.sugarman.myb.ui.activities.editProfile;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 19.10.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IEditProfileActivityView
    extends MvpView {
  void finishActivity();
}
