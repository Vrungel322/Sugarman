package com.sugarman.myb.ui.activities.newStats;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import com.arellomobile.mvp.InjectViewState;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicPresenter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 15.03.2018.
 */
@InjectViewState
public class NewStatsActivityPresenter extends BasicPresenter<INewStatsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public BarData generateBarData(List<Stats> stats) {

    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
    ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

    for (int index = 0; index < stats.size() * 2; index++) {
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

  public LineData generateLineData(List<Stats> stats, List<Integer> statsSteps, Drawable drawable) {

    LineData d = new LineData();

    ArrayList<Entry> entries = new ArrayList<Entry>();
    ArrayList<Entry> entriesDashed = new ArrayList<Entry>();

    for (int index = 0; index < stats.size(); index++) {
      //entries.add(new Entry(index + 0.5f, getRandom(15, 5)));
      entries.add(new Entry(index, statsSteps.get(index)));
      //add icon to last point of chart
      if (index == stats.size() - 1) {
        entries.add(new Entry(index, statsSteps.get(index),drawable)); // add icon to point on chart
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
    for (int index = 0; index < stats.size(); index++) {
      entriesDashed.add(new Entry(index, statsSteps.get(index) + 2000));
    }

    LineDataSet setDashed = new LineDataSet(entriesDashed, "Group Steps");
    setDashed.setColor(Color.rgb(240, 0, 0));
    setDashed.setLineWidth(2.5f);
    //setDashed.setCircleColor(Color.rgb(240, 0, 0));
    //setDashed.setCircleRadius(5f);
    //setDashed.setFillColor(Color.rgb(240, 0, 0));
    //setDashed.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    //setDashed.setDrawValues(true);
    setDashed.setValueTextSize(0f);
    //setDashed.setValueTextColor(Color.rgb(240, 0, 0));

    setDashed.enableDashedLine(10, 10, 0);

    setDashed.setAxisDependency(YAxis.AxisDependency.LEFT);
    d.addDataSet(setDashed);

    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    dataSets.add(set);
    dataSets.add(setDashed);

    return new LineData(dataSets);
  }
}
