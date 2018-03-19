package com.sugarman.myb.ui.activities.newStats;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import timber.log.Timber;

public class NewStatsActivity extends BasicActivity implements INewStatsActivityView {
  public static final int STATS_COUNT_7 = 7;
  public static final int STATS_COUNT_21 = 21;
  @InjectPresenter NewStatsActivityPresenter mPresenter;
  @BindView(R.id.chart1) CombinedChart mChart;
  @BindView(R.id.tvName) TextView mTextViewName;
  @BindView(R.id.ivAvatar) ImageView mImageViewAvatar;
  private Tracking mTracking;
  private List<Stats> mStats = new ArrayList<>();
  private List<String> mStatsDays = new ArrayList<>();
  private List<Integer> mStatsSteps = new ArrayList<>();
  private int mStatsCount = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_new_stats);
    super.onCreate(savedInstanceState);
    if (getIntent().getExtras().containsKey(Constants.TRACKING)) {
      mTracking = getIntent().getExtras().getParcelable(Constants.TRACKING);
    }
    fillByStats(STATS_COUNT_7);
    setUpUIChart();
    setUpUI();
  }

  private void setUpUI() {
    if (mTracking!=null){
      mTextViewName.setText(mTracking.getGroup().getName());
      CustomPicasso.with(this)
          .load(mTracking.getGroup().getPictureUrl())
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
    xAxis.setValueFormatter(new IAxisValueFormatter() {
      @Override public String getFormattedValue(float value, AxisBase axis) {
        return mStatsDays.get((int) value % mStatsDays.size());
      }
    });
    GestureDetector gd =
        new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

          @Override public boolean onDoubleTap(MotionEvent e) {
            Timber.e("onTouchDouble " + mStatsCount);
            if (mStatsCount == STATS_COUNT_21) {
              fillByStats(STATS_COUNT_7);
              return true;
            }
            if (mStatsCount == STATS_COUNT_7) {
              fillByStats(STATS_COUNT_21);
              return true;
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

  private void fillByStats(int statsCount) {
    mStatsCount = statsCount;
    mStats.clear();
    mStatsDays.clear();
    mStatsSteps.clear();

    mStats.addAll(SharedPreferenceHelper.getStats(mStatsCount));
    Timber.e("onTouchDouble fillByStats " + mStats.size());

    for (int i = 0; i < mStats.size(); i++) {
      if (statsCount == 21) {
        mStatsDays.add(String.valueOf(i + 1));
      }
      if (statsCount == 7) {
        mStatsDays.add(getString(R.string.day) + " " + String.valueOf(i + 1));
      }
      mStatsSteps.add(mStats.get(i).getStepsCount());
    }

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
  }

  @OnClick(R.id.ivClose) void ivCloseClicked(){
    finish();
  }
}
