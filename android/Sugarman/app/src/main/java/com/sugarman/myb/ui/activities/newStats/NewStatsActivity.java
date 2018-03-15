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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
            if (mStatsCount == 20) {
              fillByStats(7);
              return true;
            }
            if (mStatsCount == 7) {
              fillByStats(20);
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

    if (statsCount == 20) {
      for (int i = 0; i < mStats.size(); i++) {
        mStatsDays.add(String.valueOf(i + 1));
        mStatsSteps.add(mStats.get(i).getStepsCount());
      }
    }
    if (statsCount == 7) {
      for (int i = 0; i < mStats.size(); i++) {
        mStatsDays.add(mStats.get(i).getDate());
        mStatsSteps.add(mStats.get(i).getStepsCount());
      }
    }

    CombinedData data = new CombinedData();

    data.setData(generateLineData()); // line - dots
    data.setData(generateBarData()); // colomns
    //data.setData(generateDashedData()); // dots

    mChart.getXAxis().setAxisMaximum(data.getXMax() + 0.25f);
    mChart.setData(null);
    mChart.setData(data);
    mChart.invalidate();
  }

  private LineData generateLineData() {

    LineData d = new LineData();

    ArrayList<Entry> entries = new ArrayList<Entry>();
    ArrayList<Entry> entriesDashed = new ArrayList<Entry>();

    for (int index = 0; index < mStats.size(); index++) {
      //entries.add(new Entry(index + 0.5f, getRandom(15, 5)));
      entries.add(new Entry(index, mStatsSteps.get(index)));
      //add icon to last point of chart
      if (index == mStats.size() - 1) {
        entries.add(new Entry(index, mStatsSteps.get(index),
            getResources().getDrawable(R.drawable.fb_icon))); // add icon to point on chart
      }
    }

    LineDataSet set = new LineDataSet(entries, "Steps");
    set.setColor(Color.rgb(240, 238, 70));
    set.setLineWidth(2.5f);
    set.setCircleColor(Color.rgb(240, 238, 70));
    set.setCircleRadius(5f);
    set.setFillColor(Color.rgb(240, 238, 70));
    set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    set.setDrawValues(true);
    set.setValueTextSize(10f);
    set.setValueTextColor(Color.rgb(240, 238, 70));

    set.setAxisDependency(YAxis.AxisDependency.LEFT);
    d.addDataSet(set);


    //Dashed stuff
    for (int index = 0; index < mStats.size(); index++) {
      entriesDashed.add(new Entry(index, mStatsSteps.get(index)+2000));
    }

    LineDataSet setDashed = new LineDataSet(entriesDashed, "Group Steps");
    setDashed.setColor(Color.rgb(240, 0, 0));
    setDashed.setLineWidth(2.5f);
    //setDashed.setCircleColor(Color.rgb(240, 0, 0));
    //setDashed.setCircleRadius(5f);
    //setDashed.setFillColor(Color.rgb(240, 0, 0));
    //setDashed.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    //setDashed.setDrawValues(true);
    //setDashed.setValueTextSize(10f);
    //setDashed.setValueTextColor(Color.rgb(240, 0, 0));

    setDashed.enableDashedLine(10, 10, 0);

    setDashed.setAxisDependency(YAxis.AxisDependency.LEFT);
    d.addDataSet(setDashed);

    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    dataSets.add(set);
    dataSets.add(setDashed);


    return new LineData(dataSets);
  }

  private BarData generateBarData() {

    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
    ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

    for (int index = 0; index < mStats.size() * 2; index++) {
      //entries1.add(new BarEntry(0, getRandom(25, 25)));
      entries1.add(new BarEntry(index * 0.5f, 10000));

      //// stacked
      //entries2.add(new BarEntry(0, new float[] { getRandom(13, 12), getRandom(13, 12) }));
      //entries2.add(new BarEntry(0.001f, 1000));
    }

    BarDataSet set1 = new BarDataSet(entries1, "10000 Steps");
    set1.setColor(Color.rgb(0, 220, 78));
    set1.setBarBorderColor(Color.rgb(0, 220, 78));
    //set1.setValueTextColor(Color.rgb(60, 220, 78));
    set1.setValueTextSize(0f); //make text invisible
    set1.setAxisDependency(YAxis.AxisDependency.LEFT);

    //BarDataSet set2 = new BarDataSet(entries1, "Bar 2");
    //set2.setColor(Color.rgb(60, 220, 78));
    //set2.setValueTextColor(Color.rgb(60, 220, 78));
    //set2.setValueTextSize(10f);
    //set2.setAxisDependency(YAxis.AxisDependency.LEFT);

    //BarDataSet set2 = new BarDataSet(entries2, "");
    //set2.setStackLabels(new String[] { "Stack 1", "Stack 2" });
    //set2.setColors(new int[] { Color.rgb(61, 165, 255), Color.rgb(23, 197, 255) });
    //set2.setValueTextColor(Color.rgb(61, 165, 255));
    //set2.setValueTextSize(10f);
    //set2.setAxisDependency(YAxis.AxisDependency.LEFT);

    //float groupSpace = 0.06f;
    //float barSpace = 0.02f; // x2 dataset
    //float barWidth = 0.45f; // x2 dataset
    //float groupSpace = 0.006f;
    //float barSpace = 0.001f; // x2 dataset
    //float barWidth = 1f; // x2 dataset
    // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

    //BarData d = new BarData(set1, set2);
    BarData d = new BarData(set1);
    //d.setBarWidth(barWidth);

    // make this BarData object grouped
    //d.groupBars(0, groupSpace, barSpace); // start at x = 0

    return d;
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
