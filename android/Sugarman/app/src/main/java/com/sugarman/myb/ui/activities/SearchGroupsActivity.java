package com.sugarman.myb.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.GroupsAdapter;
import com.sugarman.myb.api.clients.GetTrackingsClient;
import com.sugarman.myb.api.clients.JoinGroupClient;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.listeners.ApiGetTrackingsListener;
import com.sugarman.myb.listeners.ApiJoinGroupListener;
import com.sugarman.myb.listeners.OnGroupsActionListener;
import com.sugarman.myb.models.SearchTracking;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.groupDetails.GroupDetailsActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.utils.DeviceHelper;
import java.util.ArrayList;
import java.util.List;

public class SearchGroupsActivity extends BaseActivity
    implements View.OnClickListener, OnGroupsActionListener, ApiGetTrackingsListener,
    ApiJoinGroupListener {

  private static final String TAG = SearchGroupsActivity.class.getName();

  private GroupsAdapter groupsAdapter;

  private View vNoGroups;
  private View vClearGroupName;
  private EditText etSearch;
  private RecyclerView rcvGroups;

  private GetTrackingsClient mGetTrackingsClient;
  private JoinGroupClient mJoinGroupClient;

  private int joinTrackingPosition;

  private final TextWatcher searchWatcher = new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // nothing
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      // nothing
    }

    @Override public void afterTextChanged(Editable s) {
      boolean isEmpty = TextUtils.isEmpty(s);
      vClearGroupName.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
      String query = s.toString();
      mGetTrackingsClient.getTrackings(GetTrackingsClient.AVAILABLE_TYPE, query);
    }
  };

  @Override protected void onCreate(Bundle savedStateInstance) {
    setContentView(R.layout.activity_search_groups);
    super.onCreate(savedStateInstance);

    groupsAdapter = new GroupsAdapter(this, this);

    View vBack = findViewById(R.id.iv_back);
    vClearGroupName = findViewById(R.id.iv_clear_group_name_input);
    vNoGroups = findViewById(R.id.tv_no_search_group);
    etSearch = (EditText) findViewById(R.id.et_search);
    rcvGroups = (RecyclerView) findViewById(R.id.rcv_groups);

    rcvGroups.setLayoutManager(new LinearLayoutManager(this));
    rcvGroups.setAdapter(groupsAdapter);

    etSearch.addTextChangedListener(searchWatcher);

    vClearGroupName.setOnClickListener(this);
    vBack.setOnClickListener(this);

    mGetTrackingsClient = new GetTrackingsClient();
    mJoinGroupClient = new JoinGroupClient();

    mGetTrackingsClient.getTrackings(GetTrackingsClient.AVAILABLE_TYPE, "");
    //  vNoGroups.setVisibility(View.VISIBLE);
  }

  @Override protected void onStart() {
    super.onStart();

    mJoinGroupClient.registerListener(this);
    mGetTrackingsClient.registerListener(this);
  }

  @Override protected void onStop() {
    super.onStop();

    mJoinGroupClient.unregisterListener();
    mGetTrackingsClient.unregisterListener();
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.iv_back:
        finish();
        break;
      case R.id.iv_clear_group_name_input:
        etSearch.setText("");
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onClickDialog(SugarmanDialog dialog, DialogButton button) {
    String id = dialog.getId();

    switch (id) {
      case DialogConstants.API_GET_AVAILABLE_TRACKING_FAILURE_ID:
      case DialogConstants.API_GET_UNAVAILABLE_TRACKING_FAILURE_ID:
      case DialogConstants.API_GET_MY_REQUESTS_FAILURE_ID:
        dialog.dismiss();
        finish();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onJoinGroup(int position, String trackingId, String groupId) {
    showProgressFragment();
    joinTrackingPosition = position;
    mJoinGroupClient.joinGroup(trackingId, groupId);
  }

  @Override public void onClickGroup(int position, String trackingId, String groupId) {
    openGroupDetailsActivity(trackingId);
  }

  @Override public void onApiGetTrackingsSuccess(String type, Tracking[] trackings) {
    List<SearchTracking> searchTrackings = convertTrackingToSearch(trackings);

    switch (type) {
      case GetTrackingsClient.AVAILABLE_TYPE:
        groupsAdapter.setValues(searchTrackings, true);
        vNoGroups.setVisibility(!TextUtils.isEmpty(etSearch.getText()) ? View.GONE : View.VISIBLE);
        //if (!TextUtils.isEmpty(etSearch.getText())) {
        updateResult();
        //}
        if (trackings != null && trackings.length != 0) {
          vNoGroups.setVisibility(View.GONE);
        } else {
          vNoGroups.setVisibility(View.VISIBLE);
        }
        closeProgressFragment();
        break;
      case GetTrackingsClient.UNAVAILABLE_TYPE:
        groupsAdapter.setValues(searchTrackings, false);
        if (!TextUtils.isEmpty(etSearch.getText())) {
          updateResult();
        }
        closeProgressFragment();
        break;
      default:
        Log.d(TAG, "not supported tracking type: " + type);
        break;
    }
  }

  @Override public void onApiGetTrackingsFailure(String type, String message) {
    closeProgressFragment();

    if (DeviceHelper.isNetworkConnected()) {
      switch (type) {
        case GetTrackingsClient.AVAILABLE_TYPE:
          new SugarmanDialog.Builder(this,
              DialogConstants.API_GET_AVAILABLE_TRACKING_FAILURE_ID).content(message)
              .btnCallback(this)
              .show();
          break;
        case GetTrackingsClient.UNAVAILABLE_TYPE:
          new SugarmanDialog.Builder(this,
              DialogConstants.API_GET_UNAVAILABLE_TRACKING_FAILURE_ID).content(message)
              .btnCallback(this)
              .show();
          break;
        default:
          Log.d(TAG, "not supported tracking type: " + type);
          break;
      }
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void onApiJoinGroupSuccess(Tracking result) {
    SearchTracking tracking = groupsAdapter.getValue(joinTrackingPosition);
    if (tracking != null && !tracking.isRequested()) {
      tracking.setRequested(true);
      groupsAdapter.notifyItemChanged(joinTrackingPosition);
    }

    joinTrackingPosition = -1;

    closeProgressFragment();
  }

  @Override public void onApiJoinGroupFailure(String message) {
    closeProgressFragment();

    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_JOIN_GROUP_FAILURE_ID).content(message)
          .show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  private void updateResult() {
    if (groupsAdapter.getItemCount() > 0) {
      vNoGroups.setVisibility(View.GONE);
      rcvGroups.setVisibility(View.VISIBLE);
    } else {
      vNoGroups.setVisibility(View.VISIBLE);
      rcvGroups.setVisibility(View.GONE);
    }
  }

  private List<SearchTracking> convertTrackingToSearch(Tracking[] trackings) {
    List<SearchTracking> searchTrackings = new ArrayList<>(trackings.length);
    for (Tracking tracking : trackings) {
      searchTrackings.add(new SearchTracking(tracking));
    }

    return searchTrackings;
  }

  public void openGroupDetailsActivity(String trackingId) {
    Intent intent = new Intent(this, GroupDetailsActivity.class);
    intent.putExtra(Constants.INTENT_TRACKING_ID, trackingId);
    intent.putExtra("blockchat", true);
    startActivityForResult(intent, Constants.GROUP_DETAILS_ACTIVITY_REQUEST_CODE);
  }
}

