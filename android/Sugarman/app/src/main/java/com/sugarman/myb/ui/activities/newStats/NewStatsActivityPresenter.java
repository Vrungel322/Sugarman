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
@InjectViewState public class NewStatsActivityPresenter
    extends BasicPresenter<INewStatsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public BarData generateBarData(List<Stats> stats) {
    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

    for (int index = 0; index < stats.size() * 1; index++) {
      entries1.add(new BarEntry(index, 10000));
    }

    BarDataSet set1 = new BarDataSet(entries1, "10000 Steps");
    set1.setColor(Color.argb(50, 242, 197, 197));
    set1.setValueTextSize(0f); //make text invisible
    set1.setBarBorderColor(Color.argb(0, 242, 197, 197));
    set1.setAxisDependency(YAxis.AxisDependency.LEFT);

    BarData d = new BarData(set1);
    return d;
  }

  public LineData generateLineData(List<Stats> stats, List<Integer> statsSteps, Drawable drawable) {
    LineData d = new LineData();

    ArrayList<Entry> entries = new ArrayList<Entry>();
    ArrayList<Entry> entriesDashed = new ArrayList<Entry>();

    for (int index = 0; index < stats.size(); index++) {
      entries.add(new Entry(index, statsSteps.get(index)));
      //add icon to last point of chart
      if (index == stats.size() - 1) {
        entries.add(
            new Entry(index, statsSteps.get(index)/*,drawable*/)); // add icon to point on chart
      }
    }

    LineDataSet set = new LineDataSet(entries, "Steps");
    set.setColor(Color.rgb(255, 0, 0));
    set.setLineWidth(2.5f);
    set.setCircleColor(Color.rgb(0, 0, 0));
    set.setCircleRadius(5f);
    set.setFillColor(Color.rgb(255, 0, 0));
    set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    set.setDrawValues(true);
    set.setValueTextSize(10f);
    set.setValueTextColor(Color.rgb(0, 0, 0));
    set.setAxisDependency(YAxis.AxisDependency.LEFT);
    d.addDataSet(set);

    //Dashed stuff
    for (int index = 0; index < stats.size(); index++) {
      entriesDashed.add(new Entry(index, statsSteps.get(index) + 2000));
    }

    LineDataSet setDashed = new LineDataSet(entriesDashed, "Group Steps");
    setDashed.setColor(Color.rgb(231, 145, 129));
    setDashed.setLineWidth(2.5f);
    setDashed.setValueTextSize(0f);
    setDashed.enableDashedLine(10, 10, 0);
    setDashed.setAxisDependency(YAxis.AxisDependency.LEFT);
    d.addDataSet(setDashed);

    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    dataSets.add(set);
    dataSets.add(setDashed);

    return new LineData(dataSets);
  }
}
