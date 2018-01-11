package com.sugarman.myb.ui.fragments.rescue_challenge;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.models.ChallengeRescueItem;
import com.sugarman.myb.models.ab_testing.ABTesting;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.dialogs.dialogRescueBoldMan.DialogRescueBoldMan;
import com.sugarman.myb.ui.fragments.rescue_challenge.adapters.RescueMembersAdapter;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.Converters;
import com.sugarman.myb.utils.ItemClickSupport;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.VibrationHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 06.12.2017.
 */

public class ChallengeRescueFragment extends BasicFragment implements IChallengeRescueFragmentView {
  private static final String RESCUE_CHALLENGE = "RESCUE_CHALLENGE";
  @InjectPresenter ChallengeRescueFragmentPresenter mPresenter;
  @BindView(R.id.rvMembers) RecyclerView rvMembers;
  @BindView(R.id.group_avatar) ImageView ivAvatar;
  @BindView(R.id.rescueButton) ImageView rescueButton;
  @BindView(R.id.tv_group_name) TextView mTextViewGroupName;
  @BindView(R.id.tvLeftText) TextView tvLeftText;
  @BindView(R.id.tvRightText) TextView tvRightText;
  @BindView(R.id.tvRescueCounter) TextView mTextViewRescueCount;
  @BindView(R.id.tvRescueTimer) TextView mTextViewRescueTimer;
  @BindView(R.id.cvRescueChallengeContainer) CardView cvRescueChallengeContainer;
  @BindView(R.id.llRescueArea) LinearLayout mLinearLayoutRescueArea;
  private RescueMembersAdapter adapter;
  private ChallengeRescueItem mChallengeItem;
  private Tracking mTracking;
  private CountDownTimer mTimer;
  private boolean amIFailing = false;

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

    CustomPicasso.with(mContext)
        //.load(Uri.parse(productImageUrl))
        .load(mTracking.getGroup().getPictureUrl())
        .fit()
        .centerCrop()
        .error(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropSquareTransformation())
        .transform(new MaskTransformation(mContext, R.drawable.profile_mask, false, 0xfff))
        .into(ivAvatar);

    Timber.e("onViewCreated got inside " + mTracking.getMembers().length);
    List<Member> members = new ArrayList<>();
    members.addAll(Arrays.asList(mTracking.getMembers()));
    Timber.e("onViewCreated got inside list size " + members.size());
    for (Iterator<Member> m = members.iterator(); m.hasNext(); ) {
      {
        if (m.next().getFailureStatus() == Member.FAIL_STATUS_NORMAL) {
          m.remove();
        }
      }
      Timber.e("Members size: " + members.size());
    }
    adapter = new RescueMembersAdapter(getMvpDelegate(), members);
    rvMembers.setLayoutManager(
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
    rvMembers.setAdapter(adapter);
    adapter.notifyDataSetChanged();

    ItemClickSupport.addTo(rvMembers).setOnItemClickListener((recyclerView, pos, v) -> {
      if (adapter.getItem(pos).getFailureStatus() == Member.FAIL_STATUS_SAVED){
        new SugarmanDialog.Builder(getContext(), DialogConstants.THIS_USER_HAS_SAVED_THE_GROUP).content(
            R.string.this_user_has_saved_the_group).show();
      }
      if (adapter.getItem(pos).getFailureStatus() == Member.FAIL_STATUS_FAILUER){
        YoYo.with(Techniques.Shake).duration(700).playOn(v);
        mPresenter.pokeMember(adapter.getItem(pos), mTracking);
      }
    });

    mTextViewGroupName.setText(
        String.format(getString(R.string.your_group_has_failed_thanks_to_you_and),
            mTracking.getGroup().getName()));

    mTextViewRescueCount.setText(String.format(getString(R.string.the_group_needs_x_more_rescues),
        (int) adapter.getFailureMembersCount()));

    mTimer = new CountDownTimer(
        mTracking.getRemainToFailUTCDate().getTime() - System.currentTimeMillis(), 1000) {
      @Override public void onTick(long l) {
        mTextViewRescueTimer.setText(
            String.format(getString(R.string.you_have_x_time_to_rescue_the_group),
                Converters.timeFromMilliseconds(getActivity(), l)));
      }

      @Override public void onFinish() {
        mTextViewRescueTimer.setText(
            String.format(getString(R.string.you_have_x_time_to_rescue_the_group),
                Converters.timeFromMilliseconds(getActivity(), 1L)));
      }
    }.start();

    for (Member m : mTracking.getMembers()) {
      if (m.getFailureStatus() == Member.FAIL_STATUS_FAILUER && m.getId()
          .equals(SharedPreferenceHelper.getUserId())) {
        Timber.e("Me Current user is failure");
        amIFailing = true;
        break;
      }
    }
    if(!amIFailing) {
      rescueButton.setImageResource(R.drawable.kick_kick);
      tvLeftText.setText("Kick them now");
      tvRightText.setText("For a rescue");
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mTimer.cancel();
  }

  @OnClick(R.id.cvRescueChallengeContainer) public void openGroupActivity() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openGroupDetailsActivity(mTracking.getId(), true);
    }
  }

  @OnClick(R.id.llRescueArea) public void llRescueAreaClick() {
    if (!tvLeftText.getText().equals("Kick them now")) {
      if (SharedPreferenceHelper.getAorB() == ABTesting.B) {
        DialogRescueBoldMan.newInstance(mTracking, DialogRescueBoldMan.MONEY)
            .show(getActivity().getFragmentManager(), "DialogRescueBoldMan");
      } else {
        DialogRescueBoldMan.newInstance(mTracking, DialogRescueBoldMan.INVITES)
            .show(getActivity().getFragmentManager(), "DialogRescueBoldMan");
      }
    }
    else {
      Timber.e("llRescueAreaClick poke");
      YoYo.with(Techniques.Shake).duration(700).playOn(tvLeftText);
      YoYo.with(Techniques.Shake).duration(700).playOn(tvRightText);
      YoYo.with(Techniques.Shake).duration(700).playOn(rescueButton);
      mPresenter.pokeAll(mTracking);
    }
  }

  @Override public void superKickResponse() {
    VibrationHelper.simpleVibration(getActivity(), 1300L);
  }

  @Override public void youCanNotPokeYourselfView() {
    showToastMessage(R.string.you_cant_kick_yourself);
  }
}
