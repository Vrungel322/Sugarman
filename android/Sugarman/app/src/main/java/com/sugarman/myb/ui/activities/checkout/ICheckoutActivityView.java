package com.sugarman.myb.ui.activities.checkout;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 25.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface ICheckoutActivityView
    extends MvpView {
  void finishCheckoutActivity();
}
