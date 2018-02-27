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
import com.sugarman.myb.eventbus.events.HideDots;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import timber.log.Timber;

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
  private Tracking mTracking;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_tracking_stats);
    super.onCreate(savedInstanceState);

    Timber.e("onCreate");
    App.getEventBus().post(new HideDots(true));


    rootContainer = findViewById(R.id.ll_root_container);
    vpStats = (ViewPager) findViewById(R.id.vp_stats);
    spiStats = (SquarePageIndicator) findViewById(R.id.spi_stats);
    TextView tvGroupName = (TextView) findViewById(R.id.tv_group_name);
    TextView tvDay = (TextView) findViewById(R.id.tv_day);

    mTracking = IntentExtractorHelper.getTracking(getIntent());
    if (mTracking != null) {
      trackingId = mTracking.getId();
      Group group = mTracking.getGroup();
      tvGroupName.setText(group.getName());

      float day = ((System.currentTimeMillis() - mTracking.getStartUTCDate().getTime())
          / (float) Constants.MS_IN_DAY);
      if (day <= 0) {
        // TODO: 2/23/18 here
        if(mTracking.getChallengeName().equals("Mentors Chalange")) {
          tvDay.setText(String.format(getString(R.string.challenge_day_template), (int) (day + 1)));
          Timber.e("MENTORS CHALANGE BLYAD");
        }
        else {
              tvDay.setText(R.string.warming_up);
          Timber.e("HUY PIZDA SCOVORODA MENTORS");
        }
      } else if (day - (int) day > 0) {
        if(mTracking.getChallengeName().equals("Mentors Chalange"))
          tvDay.setText(String.format(getString(R.string.challenge_day_template), (int) (day)));
        else
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
    Timber.e("onStop");
    App.getEventBus().post(new HideDots(false));


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
    int i = 0;
    //mTracking.getStartDate().equals()

    //long startTime =

    Timber.e("Start date " + mTracking.getStartDate());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    try {
      Date date = sdf.parse(mTracking.getStartDate());
      Timber.e("Start time: " + date.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    Calendar today = Calendar.getInstance();

    //long difference = today.getTimeInMillis() - calendar.getTimeInMillis();
    //int days = (int) (difference/ (1000*60*60*24));

    for (Stats s : stats) {
      Timber.e("onApiGetTrackingStatsSuccess " +s.getDayTimestamp());
      Timber.e("index " + i++ + " " + s.getStepsCount());
    }
    statsAdapter.setStats(stats,mTracking.isMentors());
    vpStats.setOffscreenPageLimit(statsAdapter.getCount());
    spiStats.setViewPager(vpStats, 0);
    Timber.e("onApiGetTrackingStatsSuccess "+statsAdapter.getTodayIndex());
    vpStats.post(() -> vpStats.setCurrentItem(statsAdapter.getTodayIndex()));
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
