package com.sugarman.myb.ui.fragments.mentors_challenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.models.ChallengeItem;
import com.sugarman.myb.ui.fragments.no_mentors_challenge.NoMentorsChallengeFragmentPresenter;

/**
 * Created by nikita on 26.10.2017.
 */

public class MentorsChallengeFragment extends BasicFragment
    implements IMentorsChallengeFragmentView {
  private static final String MENTOR_CHALLENGE = "MENTOR_CHALLENGE";
  @InjectPresenter NoMentorsChallengeFragmentPresenter mPresenter;

  @BindView(R.id.group_avatar) ImageView mImageViewGroupAvatar;
  @BindView(R.id.tv_avatar_events) TextView mTextViewNumAvatarEvents;
  @BindView(R.id.tv_group_name) TextView mTextViewGroupName;
  @BindView(R.id.best_name) TextView mTextViewBestName;
  @BindView(R.id.best_steps) TextView mTextViewBestSteps;
  @BindView(R.id.iv_best_avatar) ImageView mImageViewBestAvatar;
  @BindView(R.id.indicator_best) ImageView mImageViewIndicatorBest;
  @BindView(R.id.fastest_name) TextView mTextViewFastestName;
  @BindView(R.id.fastest_steps) TextView mTextViewFastestSteps;
  @BindView(R.id.iv_fastest_avatar) ImageView mImageViewFastestAvatar;
  @BindView(R.id.indicator_fastest) ImageView mImageViewIndicatorFastest;
  @BindView(R.id.laziest_name) TextView mTextViewLaziestName;
  @BindView(R.id.laziest_steps) TextView mTextViewLaziestSteps;
  @BindView(R.id.iv_laziest_avatar) ImageView mImageViewLaziestAvatar;
  @BindView(R.id.indicator_laziest) ImageView mImageViewLaziestIndicator;
  @BindView(R.id.all_name) TextView mTextViewAllName;
  @BindView(R.id.all_steps) TextView mTextViewAllSteps;
  @BindView(R.id.iv_all_avatar) ImageView mImageViewAllAvatar;
  @BindView(R.id.indicator_all) ImageView mImageViewIndicatorAll;
  @BindView(R.id.iv_broken_avatar_first) ImageView mImageViewBrocenAvatarFirst;
  @BindView(R.id.iv_broken_avatar_second) ImageView mImageViewBrocenAvatarSecond;
  @BindView(R.id.iv_broken_avatar_third) ImageView mImageViewBrocenAvatarThird;
  @BindView(R.id.iv_broken_avatar_fourth) ImageView mImageViewBrocenAvatarFourth;
  @BindView(R.id.progress_strip) ImageView mImageViewProgressStripe;
  @BindView(R.id.steps_total) TextView mTextViewTotalSteps;
  private ChallengeItem mChallengeItem;

  public MentorsChallengeFragment() {
    super(R.layout.fragment_mentor_challenge);
  }

  public static MentorsChallengeFragment newInstance(ChallengeItem item) {
    Bundle args = new Bundle();
    args.putParcelable(MENTOR_CHALLENGE, item);
    MentorsChallengeFragment fragment = new MentorsChallengeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mChallengeItem = getArguments().getParcelable(MENTOR_CHALLENGE);
  }
}
