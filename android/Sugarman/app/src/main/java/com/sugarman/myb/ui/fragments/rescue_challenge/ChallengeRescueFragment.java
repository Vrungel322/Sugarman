package com.sugarman.myb.ui.fragments.rescue_challenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.models.ChallengeRescueItem;

/**
 * Created by nikita on 06.12.2017.
 */

public class ChallengeRescueFragment extends BasicFragment implements IChallengeRescueFragmentView {
  private static final String RESCUE_CHALLENGE = "RESCUE_CHALLENGE";
  @InjectPresenter ChallengeRescueFragmentPresenter mPresenter;
  private ChallengeRescueItem mChallengeItem;
  private Tracking mTracking;

  public ChallengeRescueFragment() {
    super(R.layout.fragment_rescue_challenge);
  }

  public static ChallengeRescueFragment newInstance(ChallengeRescueItem item) {
    Bundle args = new Bundle();
    args.putParcelable(RESCUE_CHALLENGE, item);
    ChallengeRescueFragment fragment = new ChallengeRescueFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mChallengeItem = getArguments().getParcelable(RESCUE_CHALLENGE);
    mTracking = mChallengeItem.getTracking();
  }
}
