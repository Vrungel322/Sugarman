package com.sugarman.myb.ui.fragments.rescue_challenge;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 06.12.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IChallengeRescueFragmentView
    extends MvpView {
  void superKickResponse();

  void youCanNotPokeYourselfView();
}
