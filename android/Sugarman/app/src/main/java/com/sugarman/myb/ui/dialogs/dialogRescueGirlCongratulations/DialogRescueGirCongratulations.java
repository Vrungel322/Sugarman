package com.sugarman.myb.ui.dialogs.dialogRescueGirlCongratulations;

import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 14.12.2017.
 */

public class DialogRescueGirCongratulations extends MvpDialogFragment
    implements IDialogRescueGirlCongratulationsView {
  public static String DIALOG_RESCUE_GIRL_CONGRATULATIONS_KEY =
      "DIALOG_RESCUE_GIRL_CONGRATULATIONS_KEY";
  @InjectPresenter DialogRescueGirCongratulationsPresenter mPresenter;
  @BindView(R.id.ivCross) ImageView mImageViewCross;
  @BindView(R.id.rvSavers) RecyclerView mRecyclerViewSavers;
  @BindView(R.id.tvYouGroupRescued) TextView mTextViewYouGroupRescued;
  @BindView(R.id.ivGroupAvatar) ImageView mImageViewGroupAvatar;
  @BindView(R.id.ivWave) ImageView mImageViewWave;
  private RescueMembersAdapter mRescueMembersAdapter;
  private List<Member> savedMembers = new ArrayList<>();

  private Tracking mTracking;

  public static DialogRescueGirCongratulations newInstance(Tracking tracking) {
    Bundle args = new Bundle();
    args.putParcelable(DIALOG_RESCUE_GIRL_CONGRATULATIONS_KEY, tracking);
    DialogRescueGirCongratulations fragment = new DialogRescueGirCongratulations();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTracking = getArguments().getParcelable(DIALOG_RESCUE_GIRL_CONGRATULATIONS_KEY);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_rescue_girl_congratulations, container, false);
    ButterKnife.bind(this, rootView);
    setUpUi();
    return rootView;
  }

  private void setUpUi() {
    mRecyclerViewSavers.setLayoutManager(
        new LinearLayoutManager(mRecyclerViewSavers.getContext(), LinearLayoutManager.HORIZONTAL,
            false));
    for (Member m : mTracking.getMembers()) {
      m.setFailureStatus(Member.FAIL_STATUS_SAVED);
      Timber.e("status " + m.getFailureStatus() + " " + mTracking.getMembers().length);
      if (m.getFailureStatus() == Member.FAIL_STATUS_SAVED) {
        savedMembers.add(m);
      }
    }
    mRescueMembersAdapter = new RescueMembersAdapter(getMvpDelegate());
    //mRescueMembersAdapter.setMembers(Arrays.asList(mTracking.getMembers()));
    mRescueMembersAdapter.setMembers(savedMembers);
    mRecyclerViewSavers.setAdapter(mRescueMembersAdapter);

    mTextViewYouGroupRescued.setText(String.format(getString(R.string.you_group_has_been_rescued),
        mTracking.getGroup().getName()));

    CustomPicasso.with(mImageViewGroupAvatar.getContext())
        .load(mTracking.getGroup().getPictureUrl())
        .transform(new CropSquareTransformation())
        .transform(
            new MaskTransformation(mImageViewGroupAvatar.getContext(), R.drawable.profile_mask,
                false, 0xfff))
        .into(mImageViewGroupAvatar);
  }

  @OnClick(R.id.ivCross) public void ivCrossClick() {
    getDialog().cancel();
  }
}
