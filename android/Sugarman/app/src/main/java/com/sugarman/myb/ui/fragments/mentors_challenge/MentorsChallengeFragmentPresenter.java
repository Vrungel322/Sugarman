package com.sugarman.myb.ui.fragments.mentors_challenge;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 26.10.2017.
 */
@InjectViewState public class MentorsChallengeFragmentPresenter
    extends BasicPresenter<IMentorsChallengeFragmentView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
