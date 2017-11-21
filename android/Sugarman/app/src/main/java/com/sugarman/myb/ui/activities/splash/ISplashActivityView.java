package com.sugarman.myb.ui.activities.splash;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 21.11.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface ISplashActivityView
    extends MvpView {
}
