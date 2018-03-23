package com.sugarman.myb.ui.activities.requestsScreen;

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
import com.sugarman.myb.adapters.RequestsAdapter;
import com.sugarman.myb.api.clients.RequestManagerClient;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.eventbus.events.RequestsRemovedEvent;
import com.sugarman.myb.eventbus.events.RequestsUpdatedEvent;
import com.sugarman.myb.listeners.ApiManageRequestsListener;
import com.sugarman.myb.listeners.OnRequestsActionListener;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class RequestsActivity extends BaseActivity
    implements View.OnClickListener, OnRequestsActionListener, ApiManageRequestsListener,
    IRequestsActivityView {
  private static final String TAG = RequestsActivity.class.getName();
  @InjectPresenter RequestsActivityPresenter mPresenter;
  private RequestsAdapter requestsAdapter;

  private RequestManagerClient mRequestManagerClient;

  private View vNoRequests;
  private RecyclerView rcvRequests;

  private int actionPosition = -1;
  private boolean isNeedRefreshTrackings;

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_requests);
    super.onCreate(savedStateInstance);

    View vBack = findViewById(R.id.iv_back);
    vNoRequests = findViewById(R.id.tv_no_requests);
    rcvRequests = (RecyclerView) findViewById(R.id.rcv_requests);

    Intent intent = getIntent();
    Request[] requests = IntentExtractorHelper.getRequests(intent);
    List<Request> actualRequests = new ArrayList<>(requests.length);

    for (Request request : requests) {
      String status = request.getTracking().getStatus();
      if (!TextUtils.equals(status, Constants.STATUS_FAILED) && !TextUtils.equals(status,
          Constants.STATUS_COMPLETED)) {
        actualRequests.add(request);
      } else {
        App.getEventBus().post(new RequestsRemovedEvent(request.getId()));
      }
    }

    Collections.sort(actualRequests, Request.BY_CREATE_AT_DESC);

    if (actualRequests.isEmpty()) {
      rcvRequests.setVisibility(View.GONE);
      vNoRequests.setVisibility(View.VISIBLE);
    } else {
      requestsAdapter = new RequestsAdapter(this, this);
      rcvRequests.setVisibility(View.VISIBLE);
      vNoRequests.setVisibility(View.GONE);
      rcvRequests.setLayoutManager(new LinearLayoutManager(this));
      rcvRequests.setAdapter(requestsAdapter);
      requestsAdapter.setValue(actualRequests);

      mRequestManagerClient = new RequestManagerClient();
    }

    vBack.setOnClickListener(this);
  }

  @Override protected void onStart() {
    super.onStart();

    if (mRequestManagerClient != null) {
      mRequestManagerClient.registerListener(this);
    }
  }

  @Override protected void onStop() {
    super.onStop();

    if (mRequestManagerClient != null) {
      mRequestManagerClient.unregisterListener();
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

  @Override public void onDeclineRequest(Request request, int position) {
    showProgressFragmentTemp();
    actionPosition = position;
    //mRequestManagerClient.decline(request.getId());
    mPresenter.declineRequest(request.getId());
  }

  @Override public void declineRequestAction() {
    Request request = requestsAdapter.getValue(actionPosition);
    if (request != null) {
      App.getEventBus().post(new RequestsRemovedEvent(request.getId()));
    }

    requestsAdapter.removeItem(actionPosition);
    actionPosition = -1;
    refreshNoRequests();
    closeProgressFragment();
  }

  @Override public void onApproveRequest(Request request, int position) {
    showProgressFragmentTemp();
    actionPosition = position;
    //mRequestManagerClient.accept(request.getId());
    mPresenter.acceptRequest(request.getId());
  }

  @Override public void acceptRequestAction() {
    isNeedRefreshTrackings = true;
    Request request = requestsAdapter.getValue(actionPosition);
    if (request != null) {
      App.getEventBus().post(new RequestsRemovedEvent(request.getId()));
    }

    App.getEventBus().post(new ReportStepsEvent());

    requestsAdapter.removeItem(actionPosition);
    actionPosition = -1;
    refreshNoRequests();
    closeProgressFragment();
  }

  @Override public void errorMsg(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_ACCEPT_REQUEST_FAILURE_ID).content(
          message).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void showRequests(List<Request> requests) {
    if (requests != null) {
      Timber.e("showRequest: " + requests.size());
      List<Request> tempList = new ArrayList<>();
      for (Request inv : requests) {
        Tracking tracking = inv.getTracking();
        long startTimestamp = tracking.getStartUTCDate().getTime();
        if (startTimestamp > System.currentTimeMillis() && !tracking.isMentors()) {
          tempList.add(inv);
        }
      }
      if (requestsAdapter != null) requestsAdapter.setValue(tempList);
    }
  }

  @Override public void onApiAcceptRequestSuccess() {
    //isNeedRefreshTrackings = true;
    //Request request = requestsAdapter.getValue(actionPosition);
    //if (request != null) {
    //  App.getEventBus().post(new RequestsRemovedEvent(request.getId()));
    //}
    //
    //App.getEventBus().post(new ReportStepsEvent());
    //
    //requestsAdapter.removeItem(actionPosition);
    //actionPosition = -1;
    //refreshNoRequests();
    //closeProgressFragment();
  }

  @Override public void onApiAcceptRequestFailure(String message) {
    //closeProgressFragment();
    //if (DeviceHelper.isNetworkConnected()) {
    //  new SugarmanDialog.Builder(this, DialogConstants.API_ACCEPT_REQUEST_FAILURE_ID).content(
    //      message).show();
    //} else {
    //  showNoInternetConnectionDialog();
    //}
  }

  @Override public void onApiDeclineRequestSuccess() {
    //Request request = requestsAdapter.getValue(actionPosition);
    //if (request != null) {
    //  App.getEventBus().post(new RequestsRemovedEvent(request.getId()));
    //}
    //
    //requestsAdapter.removeItem(actionPosition);
    //actionPosition = -1;
    //refreshNoRequests();
    //closeProgressFragment();
  }

  @Override public void onApiDeclineRequestFailure(String message) {
    //closeProgressFragment();
    //if (DeviceHelper.isNetworkConnected()) {
    //  new SugarmanDialog.Builder(this, DialogConstants.API_DECLINE_REQUEST_FAILURE_ID).content(
    //      message).show();
    //} else {
    //  showNoInternetConnectionDialog();
    //}
  }

  @Subscribe public void onEvent(RequestsUpdatedEvent event) {
    requestsAdapter.setValue(event.getRequests());
    refreshNoRequests();
  }

  private void refreshNoRequests() {
    if (requestsAdapter.getItemCount() == 0) {
      rcvRequests.setVisibility(View.GONE);
      vNoRequests.setVisibility(View.VISIBLE);
    } else {
      rcvRequests.setVisibility(View.VISIBLE);
      vNoRequests.setVisibility(View.GONE);
    }
  }

  private void closeActivity() {
    Intent data = new Intent();
    data.putExtra(Constants.INTENT_IS_NEED_REFRESH_TRACKINGS, isNeedRefreshTrackings);
    setResult(RESULT_OK, data);
    finish();
  }
}
