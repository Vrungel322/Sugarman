package com.sugarman.myb.ui.activities.newStats;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class NewStatsActivity extends BasicActivity implements INewStatsActivityView {
  private final int itemcount = 12;
  protected String[] mMonths = new String[] {
      "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
  };
  @InjectPresenter NewStatsActivityPresenter mPresenter;
  @BindView(R.id.chart1) CombinedChart mChart;
  private List<Stats> mStats = new ArrayList<>();
  private List<String> mStatsDays = new ArrayList<>();
  private List<Integer> mStatsSteps = new ArrayList<>();
  private int mStatsCount = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_new_stats);
    super.onCreate(savedInstanceState);
    fillByStats(7);
    //mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
    setUpUI();
  }

  @SuppressLint("ClickableViewAccessibility") private void setUpUI() {
    mChart.getDescription().setEnabled(false);
    mChart.setBackgroundColor(Color.WHITE);
    mChart.setDrawGridBackground(false);
    mChart.setDrawBarShadow(false);
    mChart.setHighlightFullBarEnabled(false);
    mChart.setTouchEnabled(false);// enable touch gestures

    // draw bars behind lines
    mChart.setDrawOrder(new CombinedChart.DrawOrder[] {
        CombinedChart.DrawOrder.BAR,
        //CombinedChart.DrawOrder.BUBBLE,
        //CombinedChart.DrawOrder.CANDLE,
        CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
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
            if (mStatsCount == 21) {
              fillByStats(7);
              return true;
            }
            if (mStatsCount == 7) {
              fillByStats(21);
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

    CombinedData data = new CombinedData();

    data.setData(mPresenter.generateLineData(mStats,mStatsSteps,
        getResources().getDrawable(R.drawable.animation_progress_bar))); // line - dots
    data.setData(mPresenter.generateBarData(mStats)); // colomns
    //data.setData(generateDashedData()); // dots

    mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
    mChart.setData(null);
    mChart.setData(data);
    mChart.invalidate();
  }





  //protected LineData generateDashedData() {
  //
  //  LineData d = new LineData();
  //
  //  ArrayList<Entry> entries = new ArrayList<Entry>();
  //
  //  for (int index = 0; index < mStats.size(); index++) {
  //    //entries.add(new Entry(index + 0.5f, getRandom(15, 5)));
  //    entries.add(new Entry(index, mStatsSteps.get(index)+2));
  //
  //  }
  //
  //  LineDataSet set = new LineDataSet(entries, "Group Steps");
  //  set.setColor(Color.rgb(240, 0, 0));
  //  set.setLineWidth(2.5f);
  //  set.setCircleColor(Color.rgb(240, 0, 0));
  //  set.setCircleRadius(5f);
  //  set.setFillColor(Color.rgb(240, 0, 0));
  //  set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
  //  set.setDrawValues(true);
  //  set.setValueTextSize(10f);
  //  set.setValueTextColor(Color.rgb(240, 0, 0));
  //
  //  set.enableDashedLine(10, 10, 0);
  //
  //  set.setAxisDependency(YAxis.AxisDependency.LEFT);
  //  d.addDataSet(set);
  //
  //  return d;
  //}

  protected float getRandom(float range, float startsfrom) {
    return (float) (Math.random() * range) + startsfrom;
  }
}
