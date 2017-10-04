package com.sugarman.myb.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.PokeClient;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.listeners.ApiPokeListener;
import com.sugarman.myb.models.ChallengeItem;
import com.sugarman.myb.models.ChallengeMember;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;

public class StartedChallengeFragment extends ChallengeFragment implements ApiPokeListener {

  private PokeClient mPokeClient;

  private String userId;
  private int[] brokenGlassIds;

  public static StartedChallengeFragment newInstance(ChallengeItem item, int position, int total) {
    StartedChallengeFragment fragment = new StartedChallengeFragment();

    Bundle args = new Bundle();
    args.putParcelable(Constants.BUNDLE_TRACKING_ITEM, item.getTracking());
    args.putInt(Constants.BUNDLE_TRACKING_POSITION, position);
    args.putInt(Constants.BUNDLE_TRACKINS_COUNT, total);
    fragment.setArguments(args);

    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = super.onCreateView(inflater, container, savedInstanceState);

    if (root != null) {
      mPokeClient = new PokeClient();

      userId = SharedPreferenceHelper.getUserId();
      vChallengeContainer.setOnClickListener(this);

      vWillContainer.setVisibility(View.GONE);

      brokenGlassIds = SharedPreferenceHelper.getBrokenGlassIds();
    }

    return root;
  }

  @Override public void onStart() {
    super.onStart();

    mPokeClient.registerListener(this);
  }

  @Override public void onStop() {
    super.onStop();

    mPokeClient.unregisterListener();
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    super.onClick(v);
  }

  @Override Member[] getMembers() {
    return tracking.getMembers();
  }

  @Override Member[] getPendingMembers() {
    return new Member[0];
  }

  @Override public void onApiUnauthorized() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      ((BaseActivity) activity).logout();
    }
  }

  @Override public void onUpdateOldVersion() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      ((BaseActivity) activity).showUpdateOldVersionDialog();
    }
  }

  @Override public void onApiPokeSuccess() {
    DeviceHelper.vibrate();
  }

  @Override public void onApiPokeFailure(String message) {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      if (DeviceHelper.isNetworkConnected()) {
        new SugarmanDialog.Builder(activity, DialogConstants.API_POKE_FAILURE_ID).content(message)
            .show();
      } else {
        ((BaseActivity) activity).showNoInternetConnectionDialog();
      }
    }
  }

  private void clickAvatar(int num) {
    ChallengeMember member = challengeMembers[num];
    if (member != null) {

      if (TextUtils.equals(member.getId(), userId)) {
        showYouCantKickSelfDialog();
      } else if (member.isPending()) {
        showYouCantKickPendingDialog();
      } else if (member.getSteps() >= Config.MAX_STEPS_PER_DAY) {
        showUserCompletedDailyDialog();
      } else if (member.getSteps() > SharedPreferenceHelper.getStatsTodaySteps(userId)) {
        showYouCantKickMoreDialog();
      } else {
        mPokeClient.poke(member.getId(), tracking.getId());
      }
    }
  }

  private void showUserCompletedDailyDialog() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      new SugarmanDialog.Builder(activity,
          DialogConstants.THIS_USER_HAS_COMPLETED_DAILY_ID).content(
          R.string.this_user_has_completed_daily).show();
    }
  }

  private void showYouCantKickSelfDialog() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      new SugarmanDialog.Builder(activity, DialogConstants.YOU_CANT_KICK_SELF_ID).content(
          R.string.you_cant_kick_self).show();
    }
  }

  private void showYouCantKickPendingDialog() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      new SugarmanDialog.Builder(activity, DialogConstants.YOU_CANT_KICK_PENDING_ID).content(
          R.string.you_cant_kick_pending).show();
    }
  }

  private void showYouCantKickMoreDialog() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      new SugarmanDialog.Builder(activity, DialogConstants.YOU_CANT_KICK_MORE_THAT_SELF_ID).content(
          R.string.you_cant_kick_more_that_self).show();
    }
  }
}
