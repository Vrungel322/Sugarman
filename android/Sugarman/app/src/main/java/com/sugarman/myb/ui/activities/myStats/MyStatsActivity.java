package com.sugarman.myb.ui.activities.myStats;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.StatsPagerAdapter;
import com.sugarman.myb.api.clients.GetMyStatsClient;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.constants.DialogConstants;
import com.sugarman.myb.eventbus.events.StatsOpenedEvent;
import com.sugarman.myb.listeners.ApiGetMyStatsListener;
import com.sugarman.myb.listeners.OnSwipeListener;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.statsTracking.StatsTrackingActivity;
import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;
import com.sugarman.myb.ui.views.OnSwipeTouchListener;
import com.sugarman.myb.ui.views.SquarePageIndicator;
import com.sugarman.myb.ui.views.SwipeGestureListener;
import com.sugarman.myb.utils.DeviceHelper;
import java.util.List;
import timber.log.Timber;

public class MyStatsActivity extends BaseActivity
    implements View.OnClickListener, OnSwipeListener, ApiGetMyStatsListener, IMyStatsActivityView {

  private static final String TAG = StatsTrackingActivity.class.getName();
  private final Runnable opened = new Runnable() {
    @Override public void run() {
      App.getEventBus().post(new StatsOpenedEvent());
    }
  };
  @InjectPresenter MyStatsPresenter mPresenter;
  private StatsPagerAdapter statsAdapter;
  private ViewPager vpStats;
  private SquarePageIndicator spiStats;
  private View rootContainer;
  private GetMyStatsClient getMyStatsClient;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_my_stats);
    super.onCreate(savedInstanceState);
    Timber.e("onCreate");

    rootContainer = findViewById(R.id.fl_root_container);
    vpStats = (ViewPager) findViewById(R.id.vp_stats);
    spiStats = (SquarePageIndicator) findViewById(R.id.spi_stats);

    statsAdapter = new StatsPagerAdapter(getSupportFragmentManager());
    vpStats.setAdapter(statsAdapter);
    vpStats.setOnTouchListener(new OnSwipeTouchListener(this, this));

    rootContainer.setOnClickListener(this);

    getMyStatsClient = new GetMyStatsClient();
  }

  @Override protected void onStart() {
    super.onStart();
    Timber.e("onStart");

    getMyStatsClient.registerListener(this);
    showProgressFragment();
    //getMyStatsClient.fetchStats();
    mPresenter.fetchStats();
  }

  @Override protected void onStop() {
    super.onStop();

    getMyStatsClient.unregisterListener();
  }

  @Override protected void onResume() {
    super.onResume();
    Timber.e("onResume");

    rootContainer.post(opened);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, R.anim.slide_up);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.fl_root_container:
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

  @Override public void onSwipe(SwipeGestureListener.Direction direction) {
    switch (direction) {
      case up:
        onBackPressed();
        break;
    }
  }

  @Override public void onApiGetMyStatsSuccess(Stats[] stats) {
    //statsAdapter.setStats(stats);
    //for (Stats s : stats) {
    //  Timber.e("onApiGetMyStatsSuccess " + s.getDate() + " " + s.getStepsCount());
    //}
    //vpStats.setOffscreenPageLimit(statsAdapter.getCount());
    //spiStats.setViewPager(vpStats, 0);
    //vpStats.post(new Runnable() {
    //  @Override public void run() {
    //    vpStats.setCurrentItem(statsAdapter.getTodayIndex());
    //  }
    //});
    //closeProgressFragment();
  }

  @Override public void onApiGetMyStatsFailure(String message) {
    closeProgressFragment();
    if (DeviceHelper.isNetworkConnected()) {
      new SugarmanDialog.Builder(this, DialogConstants.API_GET_TRACKING_STATS_FAILURE_ID).content(
          message).btnCallback(this).show();
    } else {
      showNoInternetConnectionDialog();
    }
  }

  @Override public void showStats(List<Stats> stats) {
    if (stats != null && stats.size() != 0) {
      Timber.e("showStats size = " + stats.size());
      for (Stats s : stats) {
        if (s.getStepsCount() < 0) {
          Timber.e("showStats s.getStepsCount() = " + s.getStepsCount());

          s.setStepsCount(0);
        }
      }
      statsAdapter.setStats(stats);
      for (Stats s : stats) {
        Timber.e("onApiGetMyStatsSuccess " + s.getDate() + " " + s.getStepsCount());
      }
      vpStats.setOffscreenPageLimit(statsAdapter.getCount());
      spiStats.setViewPager(vpStats, 0);
      vpStats.post(new Runnable() {
        @Override public void run() {
          vpStats.setCurrentItem(statsAdapter.getTodayIndex());
        }
      });
      closeProgressFragment();
    }
  }
}

