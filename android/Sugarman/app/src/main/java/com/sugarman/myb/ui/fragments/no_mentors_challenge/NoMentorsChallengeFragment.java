package com.sugarman.myb.ui.fragments.no_mentors_challenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.utils.IntentExtractorHelper;

public class NoMentorsChallengeFragment extends BasicFragment
    implements INoMentorsChallengeFragmentView {

  private static final String TAG = NoMentorsChallengeFragment.class.getName();
  @BindView(R.id.card_view_no_mentors_challenge) CardView mCardViewNoMentorsChallenge;
  @BindView(R.id.tv_no_mentors_text) TextView mTextViewNoMentorsText;

  public NoMentorsChallengeFragment() {
    super(R.layout.fragment_no_mentors_challenge);
  }

  public static NoMentorsChallengeFragment newInstance() {
    NoMentorsChallengeFragment fragment = new NoMentorsChallengeFragment();

    Bundle args = new Bundle();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    position = IntentExtractorHelper.getTrackingPosition(getArguments());
    mTextViewNoMentorsText.setText(Html.fromHtml(
        getString(R.string.no_mentors_text_1)
            + " <br> <font color=\'red\'>"
            + getString(R.string.no_mentors_text_2)
            + getString(R.string.no_mentors_text_3)
            + "<br>"
            + getString(R.string.no_mentors_text_4)
            + " <font color=\'red\'>"
            + getString(R.string.no_mentors_text_5)
            + "</font>"));
  }

  @OnClick(R.id.card_view_no_mentors_challenge) public void cvNoChallengeClicked() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openMentorScreenActivity();
    }
  }
}
