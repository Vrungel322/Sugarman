package com.sugarman.myb.ui.activities.newStats;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.SharedPreferenceConstants;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

public class NewStatsActivity extends BasicActivity implements INewStatsActivityView {
  public static final int STATS_COUNT_PERSONAL_7 = 7;
  public static final int STATS_COUNT_PERSONAL_21 = 21;
  private static final int STATS_COUNT_PERSONAL_22 = 22;
  @InjectPresenter NewStatsActivityPresenter mPresenter;
  @BindView(R.id.ivAvatar) ImageView mImageViewAvatar;
  @BindView(R.id.tvName) TextView mTextViewName;
  @BindView(R.id.tvAvatarEvents) TextView mTextViewAvatarEvents;
  @BindView(R.id.tvStatsDay) TextView mTextViewStatsDay;
  @BindView(R.id.tvStatsPersonal) TextView mTextViewStatsPersonal;
  @BindView(R.id.tvStatsWeek) TextView mTextViewStatsWeek;
  @BindView(R.id.chart1) CombinedChart mChart;
  @BindView(R.id.ivStatsKm) ImageView mImageViewStatsKm;
  @BindView(R.id.ivStatsSteps) ImageView mImageViewStatsSteps;
  @BindView(R.id.ivStatsKcal) ImageView mImageViewStatsKcal;
  @BindView(R.id.tvValueKm) TextView mTextViewValueKm;
  @BindView(R.id.tvValueSteps) TextView mTextViewValueSteps;
  @BindView(R.id.tvValueKcal) TextView mTextViewValueKcal;
  @BindView(R.id.ivStatsTab) ImageView mImageViewStatsTab;
  @BindView(R.id.ivDetailDescriptionIndicator) ImageView mImageViewDetailIndicator;
  @BindView(R.id.ivMinMax) ImageView mImageViewMinMax;
  @BindView(R.id.tvMaxValue) TextView mTextViewMaxValue;
  @BindView(R.id.tvMinValue) TextView mTextViewMinValue;
  @BindView(R.id.imageView10) ImageView mImageViewAverageValue;
  @BindView(R.id.textViewAverage) TextView mTextViewAverageText;
  @BindView(R.id.tvAverageAday) TextView mTextViewAverageAday;
  @BindView(R.id.imageView11) ImageView mImageViewDaysAboveArerage;
  @BindView(R.id.textView13) TextView mTextViewAboveAverageText;
  @BindView(R.id.tvDaysAboveAverageValue) TextView mTextViewDaysAboveAverageValue;
  @BindView(R.id.clGroupMembersPreview) ConstraintLayout mConstraintLayoutGroupMembersPreview;
  @BindView(R.id.tvBestName) TextView mTextViewBestName;
  @BindView(R.id.tvBestSteps) TextView mTextViewBestSteps;
  @BindView(R.id.ivBestAvatar) ImageView mImageViewBestAvatar;
  @BindView(R.id.ivBestAvatarBorder) ImageView mImageViewBestAvatarBorder;
  @BindView(R.id.tvFastestName) TextView mTextViewFastestName;
  @BindView(R.id.tvFastestSteps) TextView mTextViewFastestSteps;
  @BindView(R.id.ivFastestAvatar) ImageView mImageViewFastestAvatar;
  @BindView(R.id.ivFastestAvatarBorder) ImageView mImageViewFastestAvatarBorder;
  @BindView(R.id.tvLaziestName) TextView mTextViewLaziestName;
  @BindView(R.id.tvLaziestSteps) TextView mTextViewLaziestSteps;
  @BindView(R.id.ivLaziestAvatar) ImageView mImageViewLaziestAvatar;
  @BindView(R.id.ivLaziestAvatarBorder) ImageView mImageViewLaziestAvatarBorder;
  @BindView(R.id.tvAllName) TextView mTextViewAllName;
  @BindView(R.id.tvAllSteps) TextView mTextViewAllSteps;
  @BindView(R.id.ivAllAvatar) ImageView mImageViewAllAvatar;
  @BindView(R.id.ivAllAvatarBorder) ImageView mImageViewAllAvatarBorder;
  private Tracking mTracking;
  private List<Stats> mStats = new ArrayList<>();
  private List<String> mStatsDays = new ArrayList<>();
  private List<Integer> mStatsSteps = new ArrayList<>();
  private int mStatsCount = 0;
  private int mCountOfStepsForLastXDays = 0;
  private List<Stats> mStatsOfTracking = new ArrayList<>();
  private boolean zeroDayremoved;
  private boolean is21stats = true;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_new_stats);
    super.onCreate(savedInstanceState);
    if (getIntent().getExtras() != null && getIntent().getExtras()
        .containsKey(Constants.TRACKING)) {
      mTracking = getIntent().getExtras().getParcelable(Constants.TRACKING);
    }
    setUpUI();
    //setUpUIChart();
  }

  private void setUpUI() {
    Timber.e("setUpUI" + (mTracking != null));
    try {
      mPresenter.startChartFlow(mTracking);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    if (mTracking != null) {
      mTextViewName.setText(mTracking.getGroup().getName());
      CustomPicasso.with(this)
          .load(mTracking.getGroup().getPictureUrl())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_group)
          .transform(new CropSquareTransformation())
          .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffffffff))
          .into(mImageViewAvatar);
      mConstraintLayoutGroupMembersPreview.setVisibility(View.VISIBLE);
      setupGroupsPreview();
    } else {
      mConstraintLayoutGroupMembersPreview.setVisibility(View.GONE);
      mTextViewName.setText(SharedPreferenceHelper.getUserName());
      CustomPicasso.with(this)
          .load(SharedPreferenceHelper.getAvatar())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_group)
          .transform(new CropSquareTransformation())
          .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffffffff))
          .into(mImageViewAvatar);
    }
  }

  @SuppressLint("ClickableViewAccessibility") private void setUpUIChart() {
    mChart.getDescription().setEnabled(false);
    mChart.setBackgroundColor(Color.WHITE);
    mChart.setDrawGridBackground(false);
    mChart.setDrawBarShadow(false);
    mChart.setHighlightFullBarEnabled(false);
    mChart.setTouchEnabled(true);// enable touch gestures

    mChart.animateXY(1500, 1500);

    // draw bars behind lines
    mChart.setDrawOrder(new CombinedChart.DrawOrder[] {
        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
    });

    Legend l = mChart.getLegend();
    l.setWordWrapEnabled(true);
    l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    l.setDrawInside(false);

    YAxis rightAxis = mChart.getAxisRight();
    rightAxis.setDrawGridLines(false);
    rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
    rightAxis.setEnabled(false);// turns of right numbers on chart

    YAxis leftAxis = mChart.getAxisLeft();
    leftAxis.setDrawGridLines(false);
    leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

    XAxis xAxis = mChart.getXAxis();
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // make text only on bottom
    xAxis.setAxisMinimum(0f);
    xAxis.setGranularity(1f);
    if (!mStatsDays.isEmpty() && mStatsDays.size() != 0) {
      //xAxis.setValueFormatter((value, axis) -> mStatsDays.get((int) value % mStatsDays.size()));
    }
    //GestureDetector gd =
    //    new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
    //
    //      @Override public boolean onDoubleTap(MotionEvent e) {
    //        Timber.e("onTouchDouble " + mStatsCount);
    //        if (mTextViewStatsDay.isSelected()) {
    //          // empty for now
    //          fillDetailsCard();
    //        }
    //        if (mTextViewStatsWeek.isSelected()) {
    //          // empty for now
    //          fillDetailsCard();
    //        }
    //        if (mTextViewStatsPersonal.isSelected()) { // only for personal will work double tapping
    //          if (mTracking == null) {
    //            if (mStatsCount == STATS_COUNT_PERSONAL_21) {
    //              fillByStatsPersonal(STATS_COUNT_PERSONAL_7);
    //              fillDetailsCard();
    //              return true;
    //            }
    //            if (mStatsCount == STATS_COUNT_PERSONAL_7) {
    //              fillByStatsPersonal(STATS_COUNT_PERSONAL_21);
    //              fillDetailsCard();
    //              return true;
    //            }
    //          } else {
    //            if (mStatsCount > STATS_COUNT_PERSONAL_7) {
    //              fillByStatsPersonalTrackingLast7Days(STATS_COUNT_PERSONAL_7,
    //                  mStatsOfTracking.subList(0, 7));
    //              fillDetailsCard();
    //              return true;
    //            }
    //            if (mStatsCount <= STATS_COUNT_PERSONAL_7) {
    //              fillByStatsPersonalTracking(mStatsOfTracking);
    //              fillDetailsCard();
    //              return true;
    //            }
    //          }
    //        }
    //
    //        return true;
    //      }
    //
    //      @Override public void onLongPress(MotionEvent e) {
    //        super.onLongPress(e);
    //      }
    //
    //      @Override public boolean onDoubleTapEvent(MotionEvent e) {
    //        return true;
    //      }
    //
    //      @Override public boolean onDown(MotionEvent e) {
    //        return true;
    //      }
    //    });
    //mChart.setOnTouchListener((v, event) -> gd.onTouchEvent(event));
    mChart.setOnChartGestureListener(new OnChartGestureListener() {
      @Override public void onChartGestureStart(MotionEvent me,
          ChartTouchListener.ChartGesture lastPerformedGesture) {

      }

      @Override public void onChartGestureEnd(MotionEvent me,
          ChartTouchListener.ChartGesture lastPerformedGesture) {

      }

      @Override public void onChartLongPressed(MotionEvent me) {

      }

      @Override public void onChartDoubleTapped(MotionEvent me) {
        mChart.fitScreen();
        if (mTextViewStatsPersonal.isSelected()
            || mTextViewStatsDay.isSelected()) { // only for personal will work double tapping
          //if (mTracking == null) {
          Timber.e("onChartDoubleTapped mStatsCount:"+mStatsCount);
          if (!is21stats) {
            is21stats = true;
            mChart.setVisibleXRange(0, STATS_COUNT_PERSONAL_21);
            mChart.moveViewToX(0);
            mChart.resetViewPortOffsets();
            mChart.invalidate();
            fillDetailsCard();
          } else {
            is21stats = false;
            mChart.setVisibleXRange(mStatsCount-7, mStatsCount);
            mChart.moveViewToX(mStatsCount-7);
            mChart.resetViewPortOffsets();
            mChart.invalidate();
            fillDetailsCard();
          }
          //} else {
          //  if (mStatsCount > STATS_COUNT_PERSONAL_7) {
          //    fillByStatsPersonalTrackingLast7Days(STATS_COUNT_PERSONAL_7,
          //        mStatsOfTracking.subList(0, 7));
          //    fillDetailsCard();
          //  }
          //  if (mStatsCount <= STATS_COUNT_PERSONAL_7) {
          //    fillByStatsPersonalTracking(mStatsOfTracking);
          //    fillDetailsCard();
          //  }
          //}
        }
      }

      @Override public void onChartSingleTapped(MotionEvent me) {

      }

      @Override
      public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

      }

      @Override public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

      }

      @Override public void onChartTranslate(MotionEvent me, float dX, float dY) {

      }
    });
  }

  private void fillDetailsCard() {
    if (mImageViewStatsKm.isSelected()) {
      fillDetailsByStatsKm(mStats);
    }
    if (mImageViewStatsSteps.isSelected()) {
      Timber.e("mImageViewStatsSteps " + mImageViewStatsSteps.isSelected());
      fillDetailsByStatsSteps(mStats);
    }
    if (mImageViewStatsKcal.isSelected()) {
      fillDetailsByStatsKcal(mStats);
    }
  }

  private void setUpKm() {
    mTextViewValueKm.setText(
        String.format(Locale.US, "%.2f km", mCountOfStepsForLastXDays * 0.000762f));
    Timber.e("setUpKm");
  }

  private void setUpSteps() {
    mTextViewValueSteps.setText(String.valueOf(mCountOfStepsForLastXDays) + " steps");
    Timber.e("setUpSteps");
  }

  private void setUpKcal() {
    mTextViewValueKcal.setText(
        String.format(Locale.US, "%.2f kcal", mCountOfStepsForLastXDays * 0.0435f));
    Timber.e("setUpKcal");
  }

  @OnClick(R.id.ivClose) void ivCloseClicked() {
    finish();
  }

  @OnClick({ R.id.tvStatsDay, R.id.tvStatsWeek, R.id.tvStatsPersonal }) void changeStatsOnChart(
      View v) {
    if (v.getId() == R.id.tvStatsDay) {
      mTextViewStatsDay.setSelected(true);
      mTextViewStatsDay.setTextColor(Color.WHITE);
      mTextViewStatsWeek.setSelected(false);
      mTextViewStatsWeek.setTextColor(Color.RED);
      mTextViewStatsPersonal.setSelected(false);
      mTextViewStatsPersonal.setTextColor(Color.RED);
      fillByStatsDay();
    }
    if (v.getId() == R.id.tvStatsWeek) {
      mTextViewStatsDay.setSelected(false);
      mTextViewStatsDay.setTextColor(Color.RED);
      mTextViewStatsWeek.setSelected(true);
      mTextViewStatsWeek.setTextColor(Color.WHITE);
      mTextViewStatsPersonal.setSelected(false);
      mTextViewStatsPersonal.setTextColor(Color.RED);
      fillByStatsWeek();
    }
    if (v.getId() == R.id.tvStatsPersonal) {
      mTextViewStatsDay.setSelected(false);
      mTextViewStatsDay.setTextColor(Color.RED);
      mTextViewStatsWeek.setSelected(false);
      mTextViewStatsWeek.setTextColor(Color.RED);
      mTextViewStatsPersonal.setSelected(true);
      mTextViewStatsPersonal.setTextColor(Color.WHITE);
      if (mTracking == null) {
        fillByStatsPersonal(STATS_COUNT_PERSONAL_21);
      } else {
        Timber.e("changeStatsOnChart " + mStatsOfTracking);
        fillByStatsPersonalTracking(mStatsOfTracking);
      }
    }
    fillDetailsCard();
  }

  private void fillByStatsPersonalTracking(List<Stats> statsOfTracking) {
    mStatsCount = statsOfTracking.size();
    mStats.clear();
    mStatsDays.clear();
    mStatsSteps.clear();
    mCountOfStepsForLastXDays = 0;

    if (statsOfTracking != null && statsOfTracking.size() != 0) {
      Timber.e("showStats size = " + statsOfTracking.size());
      for (Stats s : statsOfTracking) {
        Timber.e("showTrackingStats s.getStepsCount() = " + s.getStepsCount());
        if (s.getStepsCount() < 0) {
          //s.setStepsCount(0);
        }
      }
      int k = 0;
      Timber.e("Start date " + mTracking.getStartDate());

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      try {
        Date date = sdf.parse(mTracking.getStartDate());
        Timber.e("showTrackingStats Start time: " + date.getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }

      for (Stats s : statsOfTracking) {
        Timber.e("showTrackingStats " + s.getDayTimestamp());
        Timber.e("index " + k++ + " " + s.getStepsCount());
      }

      if (mTracking.isMentors()
          && statsOfTracking != null
          && !statsOfTracking.isEmpty()
          && !zeroDayremoved) {
        statsOfTracking.remove(0);
        zeroDayremoved = true;
      }

      mStats.addAll(statsOfTracking);
      Timber.e("onTouchDouble fillByStatsPersonal " + mStats.size());

      for (int i = 0; i < mStats.size(); i++) {
        if (mStatsCount == STATS_COUNT_PERSONAL_21) {
          mStatsDays.add(String.valueOf(i + 1));
        }
        if (mStatsCount == STATS_COUNT_PERSONAL_7) {
          mStatsDays.add(getString(R.string.day) + " " + String.valueOf(i + 1));
        }
        mStatsSteps.add(mStats.get(i).getStepsCount());
        Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);
        if (mStats.get(i).getStepsCount() != Constants.FAKE_STEPS_COUNT) {
          mCountOfStepsForLastXDays += mStats.get(i).getStepsCount();
        }
      }
      Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);

      Collections.sort(mStats, (stats, t1) -> Integer.valueOf(
          String.valueOf(stats.getDayTimestamp() / 1000 - t1.getDayTimestamp() / 1000)));

      CombinedData data = new CombinedData();

      Collections.reverse(mStats);
      data.setData(mPresenter.generateLineData(mStats, mStatsSteps,
          getResources().getDrawable(R.drawable.animation_progress_bar))); // line - dots
      data.setData(mPresenter.generateBarData(mStats)); // colomns

      //mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
      mChart.setData(null);
      mChart.setData(data);
      mChart.getBarData().setBarWidth(100);
      mChart.setDrawBarShadow(false);
      mChart.setHighlightFullBarEnabled(false);
      mChart.getBarData().setHighlightEnabled(false);
      mChart.setDrawValueAboveBar(true);
      mChart.fitScreen();
      mChart.invalidate();
      setUpKm();
      setUpSteps();
      setUpKcal();
    }
  }

  private void fillByStatsPersonalTrackingLast7Days(int last7ays, List<Stats> statsOfTracking) {
    mStatsCount = statsOfTracking.size();
    mStats.clear();
    mStatsDays.clear();
    mStatsSteps.clear();
    mCountOfStepsForLastXDays = 0;

    if (statsOfTracking != null && statsOfTracking.size() != 0) {
      Timber.e("showStats size = " + statsOfTracking.size());
      for (Stats s : statsOfTracking) {
        Timber.e("showTrackingStats s.getStepsCount() = " + s.getStepsCount());
        if (s.getStepsCount() < 0) {

          s.setStepsCount(0);
        }
      }
      int k = 0;
      Timber.e("Start date " + mTracking.getStartDate());

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
      try {
        Date date = sdf.parse(mTracking.getStartDate());
        Timber.e("showTrackingStats Start time: " + date.getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }

      for (Stats s : statsOfTracking) {
        Timber.e("showTrackingStats " + s.getDayTimestamp());
        Timber.e("index " + k++ + " " + s.getStepsCount());
      }

      if (mTracking.isMentors()
          && statsOfTracking != null
          && !statsOfTracking.isEmpty()
          && !zeroDayremoved) {
        statsOfTracking.remove(0);
        zeroDayremoved = true;
      }

      mStats.addAll(statsOfTracking);
      Timber.e("onTouchDouble fillByStatsPersonal " + mStats.size());

      for (int i = 0; i < mStats.size(); i++) {
        if (mStatsCount == STATS_COUNT_PERSONAL_21) {
          mStatsDays.add(String.valueOf(i + 1));
        }
        if (mStatsCount == STATS_COUNT_PERSONAL_7) {
          mStatsDays.add(getString(R.string.day) + " " + String.valueOf(i + 1));
        }
        mStatsSteps.add(mStats.get(i).getStepsCount());
        Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);
        if (mStats.get(i).getStepsCount() != Constants.FAKE_STEPS_COUNT) {
          mCountOfStepsForLastXDays += mStats.get(i).getStepsCount();
        }
      }
      Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);

      Collections.sort(mStats, (stats, t1) -> Integer.valueOf(
          String.valueOf(stats.getDayTimestamp() / 1000 - t1.getDayTimestamp() / 1000)));

      CombinedData data = new CombinedData();
      Collections.reverse(mStats);
      data.setData(mPresenter.generateLineData(mStats, mStatsSteps,
          getResources().getDrawable(R.drawable.animation_progress_bar))); // line - dots
      data.setData(mPresenter.generateBarData(mStats)); // colomns

      //mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
      mChart.setData(null);
      mChart.setData(data);
      mChart.getBarData().setBarWidth(100);
      mChart.setDrawBarShadow(false);
      mChart.setHighlightFullBarEnabled(false);
      mChart.getBarData().setHighlightEnabled(false);
      mChart.setDrawValueAboveBar(true);
      mChart.fitScreen();
      mChart.invalidate();
      setUpKm();
      setUpSteps();
      setUpKcal();
    }
  }

  private void fillByStatsPersonal(int statsCount) {
    mStatsCount = statsCount;
    mStats.clear();
    mStatsDays.clear();
    mStatsSteps.clear();
    mCountOfStepsForLastXDays = 0;

    mStats.addAll(SharedPreferenceHelper.getStats(mStatsCount));
    Timber.e("onTouchDouble fillByStatsPersonal " + mStats.size());

    for (int i = 0; i < mStats.size(); i++) {
      if (statsCount == STATS_COUNT_PERSONAL_21) {
        mStatsDays.add(String.valueOf(i + 1));
      }
      if (statsCount == STATS_COUNT_PERSONAL_7) {
        mStatsDays.add(getString(R.string.day) + " " + String.valueOf(i + 1));
      }
      mStatsSteps.add(mStats.get(i).getStepsCount());
      Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);
      if (mStats.get(i).getStepsCount() != Constants.FAKE_STEPS_COUNT) {
        mCountOfStepsForLastXDays += mStats.get(i).getStepsCount();
      }
    }
    Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);

    Collections.sort(mStats, (stats, t1) -> Integer.valueOf(
        String.valueOf(stats.getDayTimestamp() - t1.getDayTimestamp())));

    CombinedData data = new CombinedData();

    data.setData(mPresenter.generateLineData(mStats, mStatsSteps,
        getResources().getDrawable(R.drawable.animation_progress_bar))); // line - dots
    data.setData(mPresenter.generateBarData(mStats)); // colomns

    //mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
    mChart.setData(null);
    mChart.setData(data);
    mChart.getBarData().setBarWidth(100);
    mChart.setDrawBarShadow(false);
    mChart.setHighlightFullBarEnabled(false);
    mChart.getBarData().setHighlightEnabled(false);
    mChart.setDrawValueAboveBar(true);
    mChart.fitScreen();
    mChart.invalidate();
    setUpKm();
    setUpSteps();
    setUpKcal();
  }

  private void fillByStatsWeek() {
    //fillByStatsPersonal(7);// just mock
  }

  private void fillByStatsDay() {
    mPresenter.fetchDayStats();
  }

  @OnClick({ R.id.ivStatsKm, R.id.ivStatsSteps, R.id.ivStatsKcal })
  void changeStatsOnDescriptionDetails(View v) {
    if (v.getId() == R.id.ivStatsKm) {
      mImageViewStatsKm.setSelected(true);
      mImageViewStatsSteps.setSelected(false);
      mImageViewStatsKcal.setSelected(false);
      fillDetailsByStatsKm(mStats);
    }
    if (v.getId() == R.id.ivStatsSteps) {
      mImageViewStatsKm.setSelected(false);
      mImageViewStatsSteps.setSelected(true);
      mImageViewStatsKcal.setSelected(false);
      fillDetailsByStatsSteps(mStats);
    }
    if (v.getId() == R.id.ivStatsKcal) {
      mImageViewStatsKm.setSelected(false);
      mImageViewStatsSteps.setSelected(false);
      mImageViewStatsKcal.setSelected(true);
      fillDetailsByStatsKcal(mStats);
    }
  }

  private void fillDetailsByStatsKm(List<Stats> stats) {
    playAnim();
    mImageViewDetailIndicator.setBackgroundResource(R.drawable.stats_km_icon);
    mTextViewMaxValue.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findMaxKm(stats))));
    mTextViewMinValue.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findMinKm(stats))));
    mTextViewAverageAday.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findAverageKm(stats))));
  }

  private void fillDetailsByStatsSteps(List<Stats> stats) {
    playAnim();
    mImageViewDetailIndicator.setBackgroundResource(R.drawable.stats_step_icon);
    mTextViewMaxValue.setText(String.valueOf(mPresenter.findMaxSteps(stats)));
    mTextViewMinValue.setText(String.valueOf(mPresenter.findMinSteps(stats)));
    mTextViewAverageAday.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findAverageSteps(stats))));
  }

  private void fillDetailsByStatsKcal(List<Stats> stats) {
    playAnim();
    mImageViewDetailIndicator.setBackgroundResource(R.drawable.stats_kcal_icon);
    mTextViewMaxValue.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findMaxKcal(stats))));
    mTextViewMinValue.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findMinKcal(stats))));
    mTextViewAverageAday.setText(
        String.valueOf(String.format(Locale.US, "%.2f", mPresenter.findAverageKcal(stats))));
  }

  private void playAnim() {
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mImageViewDetailIndicator);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mImageViewStatsTab);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mImageViewMinMax);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mTextViewMaxValue);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mTextViewMinValue);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mImageViewAverageValue);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mTextViewAverageText);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mTextViewAverageAday);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mImageViewDaysAboveArerage);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mTextViewAboveAverageText);
    YoYo.with(Techniques.SlideInLeft).duration(750).playOn(mTextViewDaysAboveAverageValue);
  }

  /**
   * Heroviy metod - cod skopirovan iz ChallengeFragment
   */
  private void setupGroupsPreview() {
    final Member[] members = mTracking.getMembers();
    String str = "";
    String name = str;
    int color = 0xff54cc14;

    //Best
    if (members.length > 0) {
      Arrays.sort(members, Member.BY_STEPS_ASC);
      Member best = members[members.length - 1];
      //BestName
      str = best.getName() == null ? "" : best.getName();
      Timber.e("Best " + best.getName());
      if (str.contains(" ")) str = str.replaceAll("( +)", " ").trim();

      if (str.length() > 0 && str.contains(" ")) {
        name = str.substring(0, (best.getName().indexOf(" ")));

        mTextViewBestName.setText(name);
      } else {
        mTextViewBestName.setText(str);
      }
      //BestSteps usual user
      mTextViewBestSteps.setText(String.format(Locale.US, "%,d", best.getSteps()));
      //BestSteps user = me
      for (int i = 0; i < mTracking.getMembers().length; i++) {
        if (best.getId().equals(SharedPreferenceHelper.getUserId())) {
          mTextViewBestSteps.setText(String.format(Locale.US, "%,d",
              SharedPreferenceHelper.getReportStatsLocal(
                  SharedPreferenceHelper.getUserId())[0].getStepsCount()));
        }
      }
      //BestAvatar
      CustomPicasso.with(mImageViewBestAvatar.getContext())
          .load(best.getPictureUrl())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropCircleTransformation(0xff7ECC10, 1))
          .into(mImageViewBestAvatar);

      //BestColoring
      if (best.getSteps() < 5000) {
        color = 0xffe10f0f;
      } else if (best.getSteps() >= 5000 && best.getSteps() < 7500) {
        color = 0xffeb6117;
      } else if (best.getSteps() >= 7500 && best.getSteps() < 10000) {
        color = 0xffF6B147;
      }
      mImageViewBestAvatarBorder.setColorFilter(color);
      mTextViewBestName.setTextColor(color);
      mTextViewBestSteps.setTextColor(color);
    }

    if (mTracking.hasDailyWinner()) {
      Member fastest = mTracking.getDailySugarman().getUser();

      //FastestName
      str = fastest.getName();
      str = str.replaceAll("( +)", " ").trim();
      if (str.length() > 0) {
        if (str.contains(" ")) {
          name = str.substring(0, (fastest.getName().indexOf(" ")));
        } else {
          name = str;
        }
      }

      mTextViewFastestName.setText(name);
      for (Member f : members) {
        if (fastest.getId().equals(f.getId())) {
          mTextViewFastestName.setText(String.format(Locale.US, "%,d", f.getSteps()));
        }
      }

      //FastestSteps
      for (Member f : members) {
        if (fastest.getId().equals(f.getId())) {
          mTextViewFastestSteps.setText(String.format(Locale.US, "%,d", f.getSteps()));
        }
      }
      //this is fixing of server bug, but on client
      for (int i = 0; i < mTracking.getMembers().length; i++) {
        if (fastest.getId().equals(SharedPreferenceHelper.getUserId())) {
          mTextViewFastestSteps.setText(String.format(Locale.US, "%,d",
              SharedPreferenceHelper.getReportStatsLocal(
                  SharedPreferenceHelper.getUserId())[0].getStepsCount()));
        }
      }

      //FastestAvatar
      CustomPicasso.with(mImageViewFastestAvatar.getContext())
          .load(fastest.getPictureUrl())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropCircleTransformation(0xffff0000, 1))
          .into(mImageViewFastestAvatar);

      //FastestColoring
      if (fastest.getSteps() < 5000) {
        color = 0xffe10f0f;
      } else if (fastest.getSteps() >= 5000 && fastest.getSteps() < 7500) {
        color = 0xffeb6117;
      } else if (fastest.getSteps() >= 7500 && fastest.getSteps() < 10000) {
        color = 0xffF6B147;
      }

      mImageViewFastestAvatarBorder.setColorFilter(color);
      mTextViewFastestName.setTextColor(color);
      mTextViewFastestSteps.setTextColor(color);
    } else {
      mTextViewFastestName.setText(getResources().getString(R.string.sugarman_is));
      mTextViewFastestSteps.setText(getResources().getString(R.string.todays_fastest));
      CustomPicasso.with(mImageViewFastestAvatar.getContext())
          .load(R.drawable.sugar_next)
          //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_red_avatar)
          .transform(new CropCircleTransformation(0xffff0000, 1))
          .into(mImageViewFastestAvatar);
    }

    //Laziest
    Member laziest = members[0];

    //LaziestName
    str = laziest.getName();
    str = str.replaceAll("( +)", " ").trim();
    if (str.length() > 0 && str.contains(" ")) {
      name = str.substring(0, (laziest.getName().indexOf(" ")));
    } else {
      name = str;
    }
    mTextViewLaziestName.setText(name);

    //LaziestSteps
    mTextViewLaziestSteps.setText(String.format(Locale.US, "%,d", laziest.getSteps()));
    for (int i = 0; i < mTracking.getMembers().length; i++) {
      if (laziest.getId().equals(SharedPreferenceHelper.getUserId())) {
        mTextViewLaziestSteps.setText(String.format(Locale.US, "%,d",
            SharedPreferenceHelper.getReportStatsLocal(
                SharedPreferenceHelper.getUserId())[0].getStepsCount()));
      }
    }

    //LaziestAvatar
    if (laziest.getPictureUrl() == null
        || laziest.getPictureUrl().equals("")
        || laziest.getPictureUrl().equals(" ")) {
      laziest.setPictureUrl("https://sugarman-myb.s3.amazonaws.com/Group_New.png");
    }
    CustomPicasso.with(mImageViewLaziestAvatar.getContext())
        .load(laziest.getPictureUrl())
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropCircleTransformation(0xffff0000, 1))
        .into(mImageViewLaziestAvatar);

    //LaziestColoring
    if (laziest.getSteps() < 5000) {
      color = 0xffe10f0f;
    } else if (laziest.getSteps() >= 5000 && laziest.getSteps() < 7500) {
      color = 0xffeb6117;
    } else if (laziest.getSteps() >= 7500 && laziest.getSteps() < 10000) {
      color = 0xffF6B147;
    }
    mImageViewLaziestAvatarBorder.setColorFilter(color);
    mTextViewLaziestName.setTextColor(color);
    mTextViewLaziestSteps.setTextColor(color);

    //AllName
    str = Integer.toString(mTracking.getMembers().length) + " " + getResources().getString(
        R.string.users);
    mTextViewAllName.setText(str);

    //AllSteps
    int allSteps = 0;
    for (int i = 0; i < members.length; i++) {
      allSteps += members[i].getSteps();
    }
    mTextViewAllSteps.setText("" + String.format(Locale.US, "%,d", allSteps));

    //AllAvatar
    CustomPicasso.with(mImageViewAllAvatar.getContext())
        .load(R.drawable.white_bg)
        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropCircleTransformation(0xffff0000, 1))
        .into(mImageViewAllAvatar);
  }

  @Override public void showStats(List<Stats> statsCached) {
    mStatsCount = statsCached.size();
    changeStatsOnChart(mTextViewStatsPersonal);
    changeStatsOnDescriptionDetails(mImageViewStatsSteps);
    setUpUIChart();
  }

  @Override public void showTrackingStats(List<Stats> statsCached) {
    Timber.e("showTrackingStats statsCached size " + statsCached.size());
    mStatsCount = statsCached.size();
    mStatsOfTracking.clear();
    mStatsOfTracking.addAll(statsCached);
    changeStatsOnChart(mTextViewStatsPersonal);
    setUpUIChart();
    changeStatsOnDescriptionDetails(mImageViewStatsSteps);
  }

  @Override public void showDayStats(List<Stats> stats) {
    Timber.e("showDayStats stats size " + stats.size());
    mStatsCount = stats.size();
    mStats.clear();
    mStats.addAll(stats);
    mCountOfStepsForLastXDays=0;
    for (int i = 0; i < mStats.size(); i++) {
      Timber.e("showDayStats mCountOfStepsForLastXDays " + mStats.get(i).toString());
      if (mStats.get(i).getStepsCount() != Constants.FAKE_STEPS_COUNT) {
        mStatsSteps.add(mStats.get(i).getStepsCount());
        mCountOfStepsForLastXDays += mStats.get(i).getStepsCount();
      }
    }
    Timber.e("onTouchDouble mCountOfStepsForLastXDays " + mCountOfStepsForLastXDays);

    Collections.sort(mStats,
        (left, t1) -> (Integer.valueOf(left.getDate().replace(SharedPreferenceConstants.HOUR_, ""))
            - Integer.valueOf(t1.getDate().replace(SharedPreferenceConstants.HOUR_, ""))));

    CombinedData data = new CombinedData();

    data.setData(mPresenter.generateLineData(mStats, mStatsSteps,
        getResources().getDrawable(R.drawable.animation_progress_bar))); // line - dots
    data.setData(mPresenter.generateBarData(mStats)); // colomns

    //mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
    mChart.setData(null);
    mChart.setData(data);
    mChart.getBarData().setBarWidth(100);
    mChart.setDrawBarShadow(false);
    mChart.setHighlightFullBarEnabled(false);
    mChart.getBarData().setHighlightEnabled(false);
    mChart.setDrawValueAboveBar(true);
    mChart.fitScreen();
    mChart.invalidate();
    setUpKm();
    setUpSteps();
    setUpKcal();
    //setUpUI();
    //changeStatsOnDescriptionDetails(mImageViewStatsSteps);
  }
}
