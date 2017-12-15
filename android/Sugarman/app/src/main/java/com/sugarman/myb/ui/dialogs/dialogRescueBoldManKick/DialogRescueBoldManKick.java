package com.sugarman.myb.ui.dialogs.dialogRescueBoldManKick;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.MvpDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.ui.fragments.rescue_challenge.adapters.RescueMembersAdapter;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.Converters;
import com.sugarman.myb.utils.VibrationHelper;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 14.12.2017.
 */

public class DialogRescueBoldManKick extends MvpDialogFragment
    implements IDialogRescueBoldManKickView {
  private static final String DIALOG_RESCUE_BOLD_MAN_KICK = "DIALOG_RESCUE_BOLD_MAN_KICK";
  @InjectPresenter DialogRescueBoldManKickPresenter mPresenter;
  @BindView(R.id.group_avatar) ImageView mImageViewGroupAvatar;
  @BindView(R.id.ivCross) ImageView mImageViewCross;
  @BindView(R.id.tvFailText) TextView mTextViewFailText;
  @BindView(R.id.rvFailures) RecyclerView mRecyclerViewFailures;
  @BindView(R.id.tvTimeLeftForRescue) TextView mTextViewTimeLeftForRescue;
  @BindView(R.id.ivKick) ImageView mImageViewKick;
  @BindView(R.id.tvKickcThemNow) TextView mTextViewKickThemNow;

  private Tracking mTracking;
  private RescueMembersAdapter mRescueMembersAdapter;
  private List<Member> failures = new ArrayList<>();

  public static DialogRescueBoldManKick newInstance(Tracking tracking) {
    Bundle args = new Bundle();
    args.putParcelable(DIALOG_RESCUE_BOLD_MAN_KICK, tracking);
    DialogRescueBoldManKick fragment = new DialogRescueBoldManKick();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTracking = getArguments().getParcelable(DIALOG_RESCUE_BOLD_MAN_KICK);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_rescue_bold_man_kick, container, false);
    ButterKnife.bind(this, rootView);
    setUpUi();
    return rootView;
  }

  private void setUpUi() {
    CustomPicasso.with(mImageViewGroupAvatar.getContext())
        .load(mTracking.getGroup().getPictureUrl())
        .transform(new CropSquareTransformation())
        .transform(
            new MaskTransformation(mImageViewGroupAvatar.getContext(), R.drawable.profile_mask,
                false, 0xfff))
        .into(mImageViewGroupAvatar);

    mTextViewFailText.setText(String.format(getString(R.string.your_group_has_failed_thanks_to),
        mTracking.getGroup().getName()));

    mRecyclerViewFailures.setLayoutManager(
        new LinearLayoutManager(mRecyclerViewFailures.getContext(), LinearLayoutManager.HORIZONTAL,
            false));
    for (Member m : mTracking.getMembers()) {
      if (m.getFailureStatus() == Member.FAIL_STATUS_FAILUER
          || m.getFailureStatus() == Member.FAIL_STATUS_SAVED) {
        failures.add(m);
      }
    }
    mRescueMembersAdapter = new RescueMembersAdapter(getMvpDelegate());
    //mRescueMembersAdapter.setMembers(Arrays.asList(mTracking.getMembers()));
    mRescueMembersAdapter.setMembers(failures);
    mRecyclerViewFailures.setAdapter(mRescueMembersAdapter);

    CountDownTimer timer = new CountDownTimer(6000, 1000) {
      @Override public void onTick(long l) {
        mTextViewTimeLeftForRescue.setText(Converters.timeFromMilliseconds(getActivity(), l));
      }

      @Override public void onFinish() {
        mTextViewTimeLeftForRescue.setText(Converters.timeFromMilliseconds(getActivity(), 1L));
      }
    }.start();
  }

  @OnClick({ R.id.ivKick, R.id.tvKickcThemNow }) public void kickAllFailures() {
    YoYo.with(Techniques.Shake).duration(700).playOn(mTextViewKickThemNow);
    Timber.e("kickAllFailures " + failures.size());
    mPresenter.superPoke(failures, mTracking.getId());
  }

  @Override public void superKickResponse() {
    VibrationHelper.simpleVibration(getActivity(), 1300L);
  }
}
