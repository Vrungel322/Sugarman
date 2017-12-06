package com.sugarman.myb.ui.fragments.rescue_challenge;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 06.12.2017.
 */
@InjectViewState
public class ChallengeRescueFragmentPresenter extends
    BasicPresenter<IChallengeRescueFragmentView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
