package com.sugarman.myb.services.fetching_animation;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 02.01.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IFetchingAnimationServiceView
    extends MvpView {
}
