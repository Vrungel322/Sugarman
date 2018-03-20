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
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 15.03.2018.
 */
@InjectViewState public class NewStatsActivityPresenter
    extends BasicPresenter<INewStatsActivityView> {
  @Inject DbRepositoryStats mDbRepositoryStats;
  private boolean needToupdateData;
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchStats();
  }

  /**
   * make query if even one stat is empty (has FAKE_STEPS_COUNT), and update all stats
   */
  public void fetchStats() {
    List<Stats> statsCached = mDbRepositoryStats.getAllEntities(21);
    for (Stats s : statsCached) {
      if (s.getStepsCount() == Constants.FAKE_STEPS_COUNT) {
        needToupdateData = true;
      }
    }
    Timber.e("fetchStats needToupdateData:" + needToupdateData);

    if (!needToupdateData) {
      getViewState().showStats(statsCached);
    } else {

      Timber.e("fetchStats");
      Subscription subscription = mDataManager.fetchStats()
          .filter(statsResponseResponse -> statsResponseResponse.body().getResult() != null)
          .concatMap(
              statsResponseResponse -> Observable.just(statsResponseResponse.body().getResult()))
          //.concatMap(stats -> {
          //  Collections.reverse(Arrays.asList(stats));
          //  return Observable.just(stats);
          //})
          .concatMap(Observable::from)
          .concatMap(stats -> {
            mDbRepositoryStats.saveEntity(stats);
            return Observable.just(stats);
          })
          .toList()
          .compose(ThreadSchedulers.applySchedulers())
          .subscribe(statsList -> {
            Timber.e("fetchStats statsList.size = " + statsList.size());

            getViewState().showStats(statsList);
          }, Throwable::printStackTrace);
      addToUnsubscription(subscription);
    }
  }

  public BarData generateBarData(List<Stats> stats) {
    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

    for (int index = 0; index < stats.size() * 1; index++) {
      entries1.add(new BarEntry(index, 10000));
    }

    BarDataSet set1 = new BarDataSet(entries1, "10000 Steps");
    set1.setColor(Color.argb(20, 242, 197, 197));
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

  //STEPS
  public int findMaxSteps(List<Stats> stats) {
    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < stats.size(); i++) {
      integers.add(stats.get(i).getStepsCount());
    }
    return Collections.max(integers);
  }

  public int findMinSteps(List<Stats> stats) {
    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < stats.size(); i++) {
      integers.add(stats.get(i).getStepsCount());
    }
    return Collections.min(integers);
  }

  public float findAverageSteps(List<Stats> stats) {
    Integer integer = 0;
    for (int i = 0; i < stats.size(); i++) {
      integer += stats.get(i).getStepsCount();
    }
    //return findMinSteps(stats) + findMaxSteps(stats) / 2;
    return integer / stats.size();
  }

  //KM
  public float findMaxKm(List<Stats> stats) {
    return findMaxSteps(stats) * 0.000762f;
  }

  public float findMinKm(List<Stats> stats) {
    return (findMinSteps(stats) * 0.000762f);
  }

  public float findAverageKm(List<Stats> stats) {
    return (findAverageSteps(stats) * 0.000762f);
  }

  //KCAL
  public float findMaxKcal(List<Stats> stats) {
    return (findMaxSteps(stats) * 0.0435f);
  }

  public float findMinKcal(List<Stats> stats) {
    return (findMinSteps(stats) * 0.0435f);
  }

  public float findAverageKcal(List<Stats> stats) {
    return (findAverageSteps(stats) * 0.0435f);
  }
}
