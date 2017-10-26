package com.sugarman.myb.ui.fragments.mentors_challenge;

import android.os.Bundle;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicFragment;

/**
 * Created by nikita on 26.10.2017.
 */

public class MentorsChallengeFragment extends BasicFragment
    implements IMentorsChallengeFragmentView {
  @InjectPresenter MentorsChallengeFragmentPresenter mPresenter;

  public MentorsChallengeFragment() {
    super(R.layout.mentors_challenge_fragment);
  }

  public static MentorsChallengeFragment newInstance() {
    Bundle args = new Bundle();
    MentorsChallengeFragment fragment = new MentorsChallengeFragment();
    fragment.setArguments(args);
    return fragment;
  }
}
