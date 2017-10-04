package com.sugarman.myb.ui.activities.base;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 19.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IBaseActivityView
    extends MvpView {
  void startSplashActivity();
}