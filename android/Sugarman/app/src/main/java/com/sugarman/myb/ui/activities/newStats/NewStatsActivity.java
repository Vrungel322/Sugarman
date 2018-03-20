package com.sugarman.myb.ui.activities.newStats;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
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
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

public class NewStatsActivity extends BasicActivity implements INewStatsActivityView {
  public static final int STATS_COUNT_PERSONAL_7 = 7;
  public static final int STATS_COUNT_PERSONAL_21 = 21;
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
  private Tracking mTracking;
  private List<Stats> mStats = new ArrayList<>();
  private List<String> mStatsDays = new ArrayList<>();
  private List<Integer> mStatsSteps = new ArrayList<>();
  private int mStatsCount = 0;
  private int mCountOfStepsForLastXDays = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_new_stats);
    super.onCreate(savedInstanceState);
    if (getIntent().getExtras() != null && getIntent().getExtras()
        .containsKey(Constants.TRACKING)) {
      mTracking = getIntent().getExtras().getParcelable(Constants.TRACKING);
    }
    setUpUIChart();
    setUpUI();
  }

  private void setUpUI() {
    if (mTracking != null) {
      mTextViewName.setText(mTracking.getGroup().getName());
      CustomPicasso.with(this)
          .load(mTracking.getGroup().getPictureUrl())
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_group)
          .transform(new CropSquareTransformation())
          .transform(new MaskTransformation(this, R.drawable.profile_mask, false, 0xffffffff))
          .into(mImageViewAvatar);
    } else {
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
    mChart.animateXY(3000, 3000);

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
    xAxis.setValueFormatter((value, axis) -> mStatsDays.get((int) value % mStatsDays.size()));
    GestureDetector gd =
        new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

          @Override public boolean onDoubleTap(MotionEvent e) {
            Timber.e("onTouchDouble " + mStatsCount);
            if (mTextViewStatsDay.isSelected()) {
              // empty for now
              fillDetailsCard();
            }
            if (mTextViewStatsWeek.isSelected()) {
              // empty for now
              fillDetailsCard();
            }
            if (mTextViewStatsPersonal.isSelected()) { // only for personal will work double tapping
              if (mStatsCount == STATS_COUNT_PERSONAL_21) {
                fillByStatsPersonal(STATS_COUNT_PERSONAL_7);
                fillDetailsCard();
                return true;
              }
              if (mStatsCount == STATS_COUNT_PERSONAL_7) {
                fillByStatsPersonal(STATS_COUNT_PERSONAL_21);
                fillDetailsCard();
                return true;
              }
            }

            return true;
          }

          @Override public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
          }

          @Override public boolean onDoubleTapEvent(MotionEvent e) {
            return true;
          }

          @Override public boolean onDown(MotionEvent e) {
            return true;
          }
        });
    mChart.setOnTouchListener((v, event) -> gd.onTouchEvent(event));
  }

  private void fillDetailsCard() {
    if (mImageViewStatsKm.isSelected()){
      fillDetailsByStatsKm(mStats);
    }
    if (mImageViewStatsSteps.isSelected()){
      Timber.e("mImageViewStatsSteps "+mImageViewStatsSteps.isSelected());
      fillDetailsByStatsSteps(mStats);
    }
    if (mImageViewStatsKcal.isSelected()){
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
      fillByStatsPersonal(STATS_COUNT_PERSONAL_21);
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

    mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
    mChart.setData(null);
    mChart.setData(data);
    mChart.getBarData().setBarWidth(100);
    mChart.setDrawBarShadow(false);
    mChart.setHighlightFullBarEnabled(false);
    mChart.getBarData().setHighlightEnabled(false);
    mChart.setDrawValueAboveBar(true);
    mChart.invalidate();
    setUpKm();
    setUpSteps();
    setUpKcal();
  }

  private void fillByStatsWeek() {
    fillByStatsPersonal(7);// just mock
  }

  private void fillByStatsDay() {
    fillByStatsPersonal(7);//just mock
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

  @Override public void showStats(List<Stats> statsCached) {
    changeStatsOnChart(mTextViewStatsPersonal);
    changeStatsOnDescriptionDetails(mImageViewStatsSteps);
  }
}
