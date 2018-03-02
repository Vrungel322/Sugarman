package com.sugarman.myb.ui.activities.invitesScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.InvitesAdapter;
import com.sugarman.myb.api.clients.InviteManagerClient;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.InviteRemovedEvent;
import com.sugarman.myb.eventbus.events.InvitesUpdatedEvent;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.listeners.OnInvitesActionListener;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.AnalyticsHelper;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class InvitesActivity extends BaseActivity
    implements View.OnClickListener, /*OnInvitesActionListener,*/ /*ApiManageInvitesListener,*/ IInvitesActivityView {
  @InjectPresenter InvitesActivityPresenter mPresenter;

  private static final String TAG = InvitesActivity.class.getName();

  private InvitesAdapter invitesAdapter;

  private int actionPosition = -1;

  private View vNoInvites;
  private RecyclerView rcvInvites;

  private InviteManagerClient mInvitesManagerClient;

  private String inviteUnavailableTemplate;

  private boolean isNeedRefreshTrackings;
  private String lastAcceptTrackingId;

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_invites);
    super.onCreate(savedStateInstance);

    View vBack = findViewById(R.id.iv_back);
    vNoInvites = findViewById(R.id.tv_no_invites);
    rcvInvites = (RecyclerView) findViewById(R.id.rcv_invites);

    Intent intent = getIntent();
    Invite[] invites = IntentExtractorHelper.getInvites(intent);
    List<Invite> actualInvites = new ArrayList<>(invites.length);

    for (Invite invite : invites) {
      String status = invite.getTracking().getStatus();
      if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
          Constants.STATUS_COMPLETED)) {
        actualInvites.add(invite);
        Timber.e("Mentors " + invite.getTracking().isMentors());
      } else {
        App.getEventBus().post(new InviteRemovedEvent(invite.getId()));
      }
    }

    Collections.sort(actualInvites, Invite.BY_CREATE_AT_DESC);

    inviteUnavailableTemplate = getString(R.string.invite_is_no_available_template);

    if (actualInvites.isEmpty()) {
      rcvInvites.setVisibility(View.GONE);
      vNoInvites.setVisibility(View.VISIBLE);
    } else {
      mInvitesManagerClient = new InviteManagerClient();
      invitesAdapter = new InvitesAdapter(this, new OnInvitesActionListener() {
        @Override public void onDeclineInvite(Invite invite, int position) {
          Tracking tracking = invite.getTracking();
          long startTimestamp = tracking.getStartUTCDate().getTime();
          if (System.currentTimeMillis() - startTimestamp > Config.INVITE_TIME_LIVE
              && !tracking.isMentors()) {
            String groupName = tracking.getGroup().getName();
            Timber.e("MENTORS" + tracking.isMentors());
            showInviteUnavailableDialog(groupName);
            invitesAdapter.removeItem(position);
          } else {
            showProgressFragmentTemp();
            actionPosition = position;

            //mInvitesManagerClient.decline(invite.getId());
            mPresenter.declineInvitation(invite.getId());
          }
        }

        @Override public void onAcceptInvite(Invite invite, int position) {
          Tracking tracking = invite.getTracking();
          long startTimestamp = tracking.getStartUTCDate().getTime();
          if (System.currentTimeMillis() - startTimestamp > Config.INVITE_TIME_LIVE
              && !tracking.isMentors()) {
            Timber.e("MENTORS" + tracking.isMentors());
            String groupName = tracking.getGroup().getName();
            showInviteUnavailableDialog(groupName);
            invitesAdapter.removeItem(position);
          } else {
            showProgressFragmentTemp();
            actionPosition = position;

            //mInvitesManagerClient.accept(invite.getId());
            mPresenter.acceptInvitation(invite.getId());
          }
        }
      });
      rcvInvites.setVisibility(View.VISIBLE);
      vNoInvites.setVisibility(View.GONE);
      rcvInvites.setLayoutManager(new LinearLayoutManager(this));
      rcvInvites.setAdapter(invitesAdapter);
      invitesAdapter.setValues(actualInvites);
    }

    vBack.setOnClickListener(this);
  }

  @Override protected void onStart() {
    super.onStart();

    if (mInvitesManagerClient != null) {
      mInvitesManagerClient.registerListener(this);
    }
  }

  @Override protected void onStop() {
    super.onStop();

    if (mInvitesManagerClient != null) {
      mInvitesManagerClient.unregisterListener();
    }
  }

  @Override public void onBackPressed() {
    closeActivity();
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_back:
        closeActivity();
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  ///**
  // * Method from Adapter click DeclineInvite
  // * @param invite
  // * @param position
  // */
  //@Override public void onDeclineInvite(Invite invite, int position) {
  //  //Tracking tracking = invite.getTracking();
  //  //long startTimestamp = tracking.getStartUTCDate().getTime();
  //  //if (System.currentTimeMillis() - startTimestamp > Config.INVITE_TIME_LIVE
  //  //    && !tracking.isMentors()) {
  //  //  String groupName = tracking.getGroup().getName();
  //  //  Timber.e("MENTORS" + tracking.isMentors());
  //  //  showInviteUnavailableDialog(groupName);
  //  //  invitesAdapter.removeItem(position);
  //  //} else {
  //  //  showProgressFragmentTemp();
  //  //  actionPosition = position;
  //  //  mPresenter.declineInvitation(invite.getId());
  //  //  //mInvitesManagerClient.decline(invite.getId());
  //  //}
  //}
  //
  ///**
  // * Method from Adapter click AcceptInvite
  // * @param invite
  // * @param position
  // */
  //@Override public void onAcceptInvite(Invite invite, int position) {
  //  //Tracking tracking = invite.getTracking();
  //  //long startTimestamp = tracking.getStartUTCDate().getTime();
  //  //if (System.currentTimeMillis() - startTimestamp > Config.INVITE_TIME_LIVE
  //  //    && !tracking.isMentors()) {
  //  //  Timber.e("MENTORS" + tracking.isMentors());
  //  //  String groupName = tracking.getGroup().getName();
  //  //  showInviteUnavailableDialog(groupName);
  //  //  invitesAdapter.removeItem(position);
  //  //} else {
  //  //  showProgressFragmentTemp();
  //  //  actionPosition = position;
  //  //
  //  //  //mInvitesManagerClient.accept(invite.getId());
  //  //  mPresenter.acceptInvitation(invite.getId());
  //  //}
  //}

  @Override public void declineInviteAction() {
    Timber.e("declineInvitation " );

    Invite invite = invitesAdapter.getValue(actionPosition);
    if (invite != null) {
      App.getEventBus().post(new InviteRemovedEvent(invite.getId()));
    }

    invitesAdapter.removeItem(actionPosition);
    actionPosition = -1;
    refreshNoInvites();
    closeProgressFragment();
  }

  @Override public void errorMsg(String errorMsg) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_DECLINE_INVITE_FAILURE_ID).content(
          errorMsg).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void acceptInviteAction() {
    AnalyticsHelper.reportChallenge();
    isNeedRefreshTrackings = true;

    Invite invite = invitesAdapter.getValue(actionPosition);
    lastAcceptTrackingId = invite.getTracking().getId();

    App.getEventBus().post(new InviteRemovedEvent(invite.getId()));
    App.getEventBus().post(new ReportStepsEvent());

    invitesAdapter.removeItem(actionPosition);
    actionPosition = -1;
    refreshNoInvites();
    closeProgressFragment();
  }

  //@Override public void onApiDeclineInviteSuccess() {
  //  //Invite invite = invitesAdapter.getValue(actionPosition);
  //  //if (invite != null) {
  //  //  App.getEventBus().post(new InviteRemovedEvent(invite.getId()));
  //  //}
  //  //
  //  //invitesAdapter.removeItem(actionPosition);
  //  //actionPosition = -1;
  //  //refreshNoInvites();
  //  ////closeProgressFragment();
  //
  //}
  //
  //@Override public void onApiDeclineInviteFailure(String message) {
  //  //closeProgressFragment();
  //  //if (DeviceHelper.isNetworkConnected()) {
  //  //  new SugarmanDialog.Builder(this, DialogConstants.API_DECLINE_INVITE_FAILURE_ID).content(
  //  //      message).show();
  //  //} else {
  //  //  showNoInternetConnectionDialog();
  //  //}
  //}
  //
  //@Override public void onApiAcceptInviteSuccess() {
  //  //AnalyticsHelper.reportChallenge();
  //  //isNeedRefreshTrackings = true;
  //  //
  //  //Invite invite = invitesAdapter.getValue(actionPosition);
  //  //lastAcceptTrackingId = invite.getTracking().getId();
  //  //
  //  //App.getEventBus().post(new InviteRemovedEvent(invite.getId()));
  //  //App.getEventBus().post(new ReportStepsEvent());
  //  //
  //  //invitesAdapter.removeItem(actionPosition);
  //  //actionPosition = -1;
  //  //refreshNoInvites();
  //  //closeProgressFragment();
  //}
  //
  //@Override public void onApiAcceptInviteFailure(String message) {
  //  //closeProgressFragment();
  //  //if (DeviceHelper.isNetworkConnected()) {
  //  //  new SugarmanDialog.Builder(this, DialogConstants.API_ACCEPT_INVITE_FAILURE_ID).content(
  //  //      message).show();
  //  //} else {
  //  //  showNoInternetConnectionDialog();
  //  //}
  //}

  @Subscribe public void onEvent(InvitesUpdatedEvent event) {
    invitesAdapter.setValues(event.getInvites());
    refreshNoInvites();
  }

  private void refreshNoInvites() {
    if (invitesAdapter.getItemCount() == 0) {
      rcvInvites.setVisibility(View.GONE);
      vNoInvites.setVisibility(View.VISIBLE);
    } else {
      rcvInvites.setVisibility(View.VISIBLE);
      vNoInvites.setVisibility(View.GONE);
    }
  }

  private void showInviteUnavailableDialog(String groupName) {
    new SugarmanDialog.Builder(this, DialogConstants.INVITE_IS_NOT_AVAILABLE_ID).content(
        String.format(inviteUnavailableTemplate, groupName)).show();
  }

  private void closeActivity() {
    Intent data = new Intent();
    data.putExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS, isNeedRefreshTrackings);
    data.putExtra(Constants.INTENT_LAST_ACCEPT_INVITE_TRACKING_ID, lastAcceptTrackingId);
    setResult(RESULT_OK, data);
    finish();
  }
}
