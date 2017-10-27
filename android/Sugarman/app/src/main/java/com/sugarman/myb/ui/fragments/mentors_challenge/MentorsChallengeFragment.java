package com.sugarman.myb.ui.fragments.mentors_challenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.ui.fragments.no_mentors_challenge.NoMentorsChallengeFragmentPresenter;

/**
 * Created by nikita on 26.10.2017.
 */

public class MentorsChallengeFragment extends BasicFragment
    implements IMentorsChallengeFragmentView {
  @InjectPresenter NoMentorsChallengeFragmentPresenter mPresenter;

  public MentorsChallengeFragment() {
    super(R.layout.mentors_challenge_fragment);
  }

  public static MentorsChallengeFragment newInstance() {
    Bundle args = new Bundle();
    MentorsChallengeFragment fragment = new MentorsChallengeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }
}
