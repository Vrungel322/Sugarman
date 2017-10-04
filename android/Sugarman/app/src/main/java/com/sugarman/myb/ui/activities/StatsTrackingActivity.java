package com.sugarman.myb.ui.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.StatsPagerAdapter;
import com.sugarman.myb.api.clients.GetTrackingStatsClient;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.StatsOpenedEvent;
import com.sugarman.myb.listeners.ApiGetTrackingStatsListener;
import com.sugarman.myb.listeners.OnSwipeListener;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.OnSwipeTouchListener;
import com.sugarman.myb.ui.views.SquarePageIndicator;
import com.sugarman.myb.ui.views.SwipeGestureListener.Direction;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;

public class StatsTrackingActivity extends BaseActivity
    implements View.OnClickListener, OnSwipeListener, ApiGetTrackingStatsListener {

  private static final String TAG = StatsTrackingActivity.class.getName();

  private StatsPagerAdapter statsAdapter;

  private ViewPager vpStats;
  private SquarePageIndicator spiStats;
  private View rootContainer;

  private GetTrackingStatsClient getTrackingStatsClient;

  private String trackingId;

  private final Runnable opened = new Runnable() {
    @Override public void run() {
      App.getEventBus().post(new StatsOpenedEvent());
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_tracking_stats);
    super.onCreate(savedInstanceState);

    rootContainer = findViewById(R.id.ll_root_container);
    vpStats = (ViewPager) findViewById(R.id.vp_stats);
    spiStats = (SquarePageIndicator) findViewById(R.id.spi_stats);
    TextView tvGroupName = (TextView) findViewById(R.id.tv_group_name);
    TextView tvDay = (TextView) findViewById(R.id.tv_day);

    Tracking tracking = IntentExtractorHelper.getTracking(getIntent());
    if (tracking != null) {
      trackingId = tracking.getId();
      Group group = tracking.getGroup();
      tvGroupName.setText(group.getName());

      float day = ((System.currentTimeMillis() - tracking.getStartUTCDate().getTime())
          / (float) Constants.MS_IN_DAY);
      if (day <= 0) {
        tvDay.setText(R.string.warming_up);
      } else if (day - (int) day > 0) {
        tvDay.setText(String.format(getString(R.string.challenge_day_template), (int) (day + 1)));
      } else {
        tvDay.setText(String.format(getString(R.string.challenge_day_template), (int) (day)));
      }
    }

    statsAdapter = new StatsPagerAdapter(getSupportFragmentManager());
    vpStats.setAdapter(statsAdapter);
    vpStats.setOnTouchListener(new OnSwipeTouchListener(this, this));

    rootContainer.setOnClickListener(this);

    getTrackingStatsClient = new GetTrackingStatsClient();
  }

  @Override protected void onStart() {
    super.onStart();

    getTrackingStatsClient.registerListener(this);
    showProgressFragment();
    getTrackingStatsClient.getTrackingStats(trackingId);
  }

  @Override protected void onStop() {
    super.onStop();

    getTrackingStatsClient.unregisterListener();
  }

  @Override protected void onResume() {
    super.onResume();

    rootContainer.post(opened);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, R.anim.slide_up);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.ll_root_container:
        onBackPressed();
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
      case DialogConstants.API_GET_TRACKING_STATS_FAILURE_ID:
        dialog.dismiss();
        onBackPressed();
        break;
      default:
        super.onClickDialog(dialog, button);
        break;
    }
  }

  @Override public void onSwipe(Direction direction) {
    switch (direction) {
      case up:
        onBackPressed();
        break;
    }
  }

  @Override public void onApiGetTrackingStatsSuccess(Stats[] stats) {
    statsAdapter.setStats(stats);
    vpStats.setOffscreenPageLimit(statsAdapter.getCount());
    spiStats.setViewPager(vpStats, 0);
    vpStats.post(new Runnable() {
      @Override public void run() {
        vpStats.setCurrentItem(statsAdapter.getTodayIndex());
      }
    });
    closeProgressFragment();
  }

  @Override public void onApiGetTrackingStatsFailure(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_GET_TRACKING_STATS_FAILURE_ID).content(
          message).btnCallback(this).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }
}
