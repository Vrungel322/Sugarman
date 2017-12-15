package com.sugarman.myb.ui.fragments.rescue_challenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.models.ChallengeRescueItem;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.ui.fragments.rescue_challenge.adapters.RescueMembersAdapter;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.Converters;
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
  @BindView(R.id.tv_group_name) TextView mTextViewGroupName;
  @BindView(R.id.tvRescueCounter) TextView mTextViewRescueCount;
  @BindView(R.id.tvRescueTimer) TextView mTextViewRescueTimer;
  @BindView(R.id.cvRescueChallengeContainer) CardView cvRescueChallengeContainer;
  RescueMembersAdapter adapter;
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

    mTextViewGroupName.setText(
        String.format(getString(R.string.your_group_has_failed_thanks_to_you_and),
            mTracking.getGroup().getName()));

    mTextViewRescueCount.setText(String.format(getString(R.string.the_group_needs_x_more_rescues),
        (int) adapter.getItemCount()));

    //mTextViewRescueTimer.setText(
    //    String.format(getString(R.string.you_have_x_time_to_rescue_the_group),
    //        Converters.timeFromMilliseconds(getContext(),
    //            mTracking.getRemainToFailUTCDate().getTime())));
  }

  @OnClick(R.id.cvRescueChallengeContainer) public void openGroupActivity() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openGroupDetailsActivity(mTracking.getId(), true);
    }
  }
}
