package com.sugarman.myb.ui.dialogs.dialogRescueBoldMan;

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
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.ui.fragments.rescue_challenge.adapters.RescueMembersAdapter;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.Converters;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 11.12.2017.
 */

public class DialogRescueBoldMan extends MvpDialogFragment implements IDialogRescueBoldManView {
  private static final String DIALOG_RESCUE_BOLD_MAN = "DIALOG_RESCUE_BOLD_MAN";
  @InjectPresenter DialogRescueBoldManPresenter mPresenter;
  @BindView(R.id.group_avatar) ImageView mImageViewGroupAvatar;
  @BindView(R.id.ivCross) ImageView mImageViewCross;
  @BindView(R.id.tvFailText) TextView mTextViewFailText;
  @BindView(R.id.rvFailures) RecyclerView mRecyclerViewFailures;
  @BindView(R.id.tvTimeLeftForRescue) TextView mTextViewTimeLeftForRescue;
  private Tracking mTracking;
  private RescueMembersAdapter mRescueMembersAdapter;
  private List<Member> failures = new ArrayList<>();

  public static DialogRescueBoldMan newInstance(Tracking tracking) {
    Bundle args = new Bundle();
    args.putParcelable(DIALOG_RESCUE_BOLD_MAN, tracking);
    DialogRescueBoldMan fragment = new DialogRescueBoldMan();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTracking = getArguments().getParcelable(DIALOG_RESCUE_BOLD_MAN);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_rescue_bold_man, container, false);
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
      if (m.getIsFailer()) {
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
        mTextViewTimeLeftForRescue.setText(Converters.timeFromMilliseconds(getActivity(), 0L));
      }
    }.start();
  }

  @OnClick(R.id.ivCross) public void ivCrossClick() {
    getDialog().cancel();
  }
}
