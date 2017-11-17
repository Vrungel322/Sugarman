package com.sugarman.myb.ui.fragments.no_mentors_challenge;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.ui.fragments.mentors_challenge.IMentorsChallengeFragmentView;

/**
 * Created by nikita on 26.10.2017.
 */
@InjectViewState public class NoMentorsChallengeFragmentPresenter
    extends BasicPresenter<IMentorsChallengeFragmentView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
