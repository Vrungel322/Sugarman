package com.sugarman.myb.ui.activities.mainScreeen;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 06.10.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IMainActivityView
    extends MvpView {
}
