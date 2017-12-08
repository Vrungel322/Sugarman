package com.sugarman.myb.ui.fragments.rescue_challenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.models.ChallengeRescueItem;
import com.sugarman.myb.ui.fragments.rescue_challenge.adapters.RescueMembersAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 06.12.2017.
 */

public class ChallengeRescueFragment extends BasicFragment implements IChallengeRescueFragmentView {
  private static final String RESCUE_CHALLENGE = "RESCUE_CHALLENGE";
  @InjectPresenter ChallengeRescueFragmentPresenter mPresenter;
  @BindView(R.id.rvMembers) RecyclerView rvMembers;
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
    Timber.e("onViewCreated got inside "  + mTracking.getMembers().length);
    List<Member> members = Arrays.asList(mTracking.getMembers());
    Timber.e("onViewCreated got inside list size "  + members.size());
    adapter = new RescueMembersAdapter(getMvpDelegate(), members, view.getContext());
    rvMembers.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false));
    rvMembers.setAdapter(adapter);
    adapter.notifyDataSetChanged();


  }
}
