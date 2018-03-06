package com.sugarman.myb.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.eventbus.events.DebugRealStepAddedEvent;
import com.sugarman.myb.eventbus.events.DebugRefreshStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRequestStepsEvent;
import com.sugarman.myb.ui.activities.myStats.MyStatsActivity;
import com.sugarman.myb.ui.activities.statsTracking.StatsTrackingActivity;
import com.sugarman.myb.ui.views.CustomFontTextView;
import com.sugarman.myb.ui.views.VerticalIndicatorView;
import com.sugarman.myb.utils.IntentExtractorHelper;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class StatsWeekFragment extends BaseFragment {

  private static final String TAG = StatsWeekFragment.class.getName();

  private TextView tvTodaySteps;
  private VerticalIndicatorView vivToday;

  private int todaySteps;
  private int animationIndex = 0;
  private int animationSteps = 0;
  private boolean isAnimationRunned = false;
  private boolean isToday = false;

  public static StatsWeekFragment newInstance(Stats[] item, int position, boolean isMentors) {
    StatsWeekFragment fragment = new StatsWeekFragment();

    Bundle args = new Bundle();
    args.putParcelableArray(Constants.BUNDLE_MY_STATS, item);
    args.putInt(Constants.BUNDLE_POSITION, position);
    args.putBoolean(Constants.IS_MENTORS, isMentors);
    fragment.setArguments(args);

    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_week_stats, container, false);

    LinearLayout llIndicatorsContainer =
        (LinearLayout) root.findViewById(R.id.ll_indicator_container);
    LinearLayout llDaysContainer = (LinearLayout) root.findViewById(R.id.ll_days_container);

    Bundle args = getArguments();
    Stats[] stats = IntentExtractorHelper.getStats(args);
    int position = IntentExtractorHelper.getPosition(args);

    Context context = getContext();
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();

    int darkGray = ContextCompat.getColor(context, R.color.dark_gray);
    int gray = ContextCompat.getColor(context, R.color.gray);
    int red = ContextCompat.getColor(context, R.color.red);

    String dayTemplate = getString(R.string.stats_day_template);

    float stepsTextSize = resources.getDimension(R.dimen.stats_steps_text_size) / metrics.density;
    float daysTextSize =
        resources.getDimensionPixelSize(R.dimen.stats_days_text_size) / metrics.density;
    //float stepsTodayTextSize = resources.getDimension(R.dimen.stats_steps_today_text_size) / metrics.density;
    TypedValue value = new TypedValue();
    resources.getValue(R.dimen.stats_steps_text_size, value, true);

    Calendar today = GregorianCalendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);

    long todayTimestamp = today.getTimeInMillis();

    int todayYear = today.get(Calendar.YEAR);
    int todayMonth = today.get(Calendar.MONTH);
    int todayDayOfMonth = today.get(Calendar.DAY_OF_MONTH);

    Calendar day = GregorianCalendar.getInstance();
    LinearLayout.LayoutParams dayContainerParams =
        new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
    LinearLayout.LayoutParams dayParams =
        new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);

    int totalStats = stats.length;

    for (int i = 0; i < totalStats; i++) {
      Stats dayStats = stats[i];
      View vDayStats = inflater.inflate(R.layout.layout_stats_indicator, null);
      vDayStats.setLayoutParams(dayContainerParams);

      VerticalIndicatorView vivDay = (VerticalIndicatorView) vDayStats.findViewById(R.id.viv_steps);
      CustomFontTextView tvSteps = (CustomFontTextView) vDayStats.findViewById(R.id.tv_steps);

      CustomFontTextView tvDay = new CustomFontTextView(context);
      tvDay.setLayoutParams(dayParams);
      tvDay.setGravity(Gravity.CENTER_HORIZONTAL);

      llIndicatorsContainer.addView(vDayStats);
      llDaysContainer.addView(tvDay);

      int steps = dayStats.getStepsCount();
      Timber.e("steps " + steps + " " + dayStats.getDayTimestamp());
      long dayTimestamp = dayStats.getDayTimestamp();
      if(dayTimestamp==-1)
        dayTimestamp = 0;

      tvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, daysTextSize);

      if (dayTimestamp != -1) {
        day.setTimeInMillis(dayTimestamp);
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        int dayOfMonth = 0;

        dayOfMonth = day.get(Calendar.DAY_OF_MONTH);

        Timber.e("DATE FOR BULLSHIT " + year + " " + month + " " + dayOfMonth);

        if (todayYear == year && todayMonth == month && todayDayOfMonth == dayOfMonth) {

          isToday = true;
          Timber.e("Date today is equal");
          tvSteps.setTextColor(darkGray);
          tvSteps.setTypeface(R.string.font_roboto_bold);
          tvTodaySteps = tvSteps;
          vivToday = vivDay;
          todaySteps = steps;
          //tvSteps.setTextSize(TypedValue.COMPLEX_UNIT_SP, stepsTodayTextSize);
          vivToday.post(new Runnable() {
            @Override public void run() {
              animateTodaySteps(todaySteps);
            }
          });
        } else {
          Timber.e("Date today is equal not");
          tvSteps.setText(String.valueOf(steps));
          vivDay.updateIndicator(Config.MAX_STEPS_STATS, steps);

          tvSteps.setTextColor(gray);
          tvSteps.setTypeface(R.string.font_roboto_regular);
          //tvSteps.setTextSize(TypedValue.COMPLEX_UNIT_SP, stepsTextSize);
        }

        tvSteps.setTextSize(TypedValue.COMPLEX_UNIT_SP, stepsTextSize);

      }

      if (todayTimestamp <= dayTimestamp|| dayTimestamp == 0) {
        tvDay.setTextColor(gray);
      } else {
        tvDay.setTextColor(darkGray);
      }
      Activity activity = getActivity();
      String textFirstItem = "";
      int numberRow = 0;
      if (activity != null
          && activity instanceof StatsTrackingActivity
          && !getArguments().getBoolean(Constants.IS_MENTORS)) {
        textFirstItem = activity.getString(R.string.warming_up_caps);
        numberRow = i;
      } else if ((activity != null && activity instanceof MyStatsActivity)
          || getArguments().getBoolean(Constants.IS_MENTORS)) {
        textFirstItem = String.format(dayTemplate, 1);
        numberRow = i + 1;
      }

      if (position == 0 && i == 0 && getActivity() != null) {
        tvDay.setTextColor(red);
        tvDay.setTypeface(R.string.font_roboto_bold);
        tvDay.setText(textFirstItem);
      } else if (position * Config.DAYS_ON_STATS_SCREEN + i == Config.SHOWING_DAYS_STATS - 1) {
        tvDay.setTypeface(R.string.font_roboto_regular);
        tvDay.setText(R.string.last_day);
      } else {
        tvDay.setTypeface(R.string.font_roboto_regular);
        tvDay.setText(
            String.format(dayTemplate, numberRow + (position * Config.DAYS_ON_STATS_SCREEN)));
      }
    }

    if (!App.getEventBus().isRegistered(this)) {
      App.getEventBus().register(this);
    }

    return root;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    try {
      App.getEventBus().unregister(this);
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure unregister event bus", e);
    }
  }

  @Override public void onStart() {
    super.onStart();
    App.getEventBus().post(new DebugRequestStepsEvent(todaySteps));
  }

  @Subscribe public void onEvent(DebugRefreshStepsEvent event) {
    todaySteps = event.getStartValue() + event.getRealSteps();
    if (tvTodaySteps != null && vivToday != null && !isAnimationRunned) {
      updateTodaySteps(todaySteps);
    }
  }

  @Subscribe public void onEvent(DebugRealStepAddedEvent event) {
    todaySteps = event.getStepsCalculated();
    if (tvTodaySteps != null && vivToday != null && !isAnimationRunned) {
      updateTodaySteps(todaySteps);
    }
  }

  private void animateTodaySteps(final int todaySteps) {
    final Handler handler = App.getHandlerInstance();
    final int iterations = (todaySteps < Config.ANIMATION_COUNT_ITERATIONS) ? todaySteps
        : Config.ANIMATION_COUNT_ITERATIONS;
    if (iterations > 0) {
      final int period = todaySteps / iterations;
      animationIndex = 0;
      animationSteps = 0;

      isAnimationRunned = true;
      handler.postDelayed(new Runnable() {
        @Override public void run() {
          if (animationIndex < iterations) {
            animationSteps += period;
            animationSteps = animationSteps > StatsWeekFragment.this.todaySteps
                ? StatsWeekFragment.this.todaySteps : animationSteps;
            updateTodaySteps(animationSteps);
            handler.postDelayed(this, Config.MAIN_ANIMATION_STEPS_ITERATION_DELAY);
          } else {
            animationSteps = StatsWeekFragment.this.todaySteps;
            updateTodaySteps(animationSteps);
            isAnimationRunned = false;
          }
          animationIndex++;
        }
      }, Config.MAIN_ANIMATION_STEPS_ITERATION_DELAY);
    } else {
      updateTodaySteps(0);
    }
  }

  private void updateTodaySteps(int steps) {
    if (tvTodaySteps != null && vivToday != null) {
      tvTodaySteps.setText(String.valueOf(steps));
      vivToday.updateIndicator(Config.MAX_STEPS_STATS, steps);
    }
  }

  public boolean isToday() {
    return isToday;
  }
}
