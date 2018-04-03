package com.sugarman.myb.ui.activities.newStats;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.arellomobile.mvp.InjectViewState;
import com.clover_studio.spikachatmodule.base.SingletonLikeApp;
import com.clover_studio.spikachatmodule.models.User;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.models.chat_refactor.Message;
import com.sugarman.myb.models.chat_refactor.SeenBy;
import com.sugarman.myb.utils.DataUtils;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 15.03.2018.
 */
@InjectViewState public class NewStatsActivityPresenter
    extends BasicPresenter<INewStatsActivityView> {
  public static final float KM_COEFFICIENT = 0.000762f;
  public static final float KCAL_COEFFICIENT = 0.0303f;
  @Inject DbRepositoryStats mDbRepositoryStats;
  ArrayList<Entry> entries = new ArrayList<Entry>();
  ArrayList<Entry> entriesDashed = new ArrayList<Entry>();
  ArrayList<Stats> sempStat = new ArrayList<Stats>();
  private boolean needToupdateData; // if on charts my stats data
  private boolean needToUpdateDataTracking; // if on charts tracking data

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void fetchDayStats(float coefficient) {
    Subscription subscription =
        Observable.just(SharedPreferenceHelper.getStepsPerDay())
            .flatMap(stringIntegerSortedMap -> {
              List<Stats> stats = new ArrayList<>();
              for (Map.Entry<String, Integer> entry : stringIntegerSortedMap.entrySet()) {
                stats.add(
                    new Stats(0, entry.getKey(), "H_" + entry.getKey(), entry.getValue(), 1L));
              }
              return Observable.just(stats);
            })
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(stats -> getViewState().showDayStats(stats, coefficient),
                Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void startChartFlow(@Nullable Tracking tracking, Context context) throws ParseException {
    WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
    if (tracking != null) {
      fetchTrackingStats(tracking.getId(), tracking, tracking.isMentors());
      fetchMessages(tracking.getId(), "0", SingletonLikeApp.getInstance()
          .getSharedPreferences(contextWeakReference.get())
          .getToken());
      fetchAverageStats(tracking);
    } else {
      fetchStats();
    }
  }

  public void fetchTrackingStats(String trackingId, Tracking tracking, boolean isMentors)
      throws ParseException {
    String startDate;
    String userJoinDate = "";
    if (!isMentors) {
      startDate = new SimpleDateFormat("yyyy-MM-dd").format(
          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(tracking.getStartDate()));
    } else {

      Member[] members = tracking.getMembers();

      for (Member m : members) {
        if (m.getId().equals(SharedPreferenceHelper.getUserId())) {
          userJoinDate = m.getCreatedAt();
        }
      }

      Timber.e("User join date " + userJoinDate);

      if (userJoinDate == null || userJoinDate.isEmpty()) userJoinDate = tracking.getStartDate();

      startDate = new SimpleDateFormat("yyyy-MM-dd").format(
          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(userJoinDate));
    }
    //Timber.e("fetchTrackingStats startDate: " + startDate);

    int diff = DataUtils.getDateDiff(
        DataUtils.subtractDays(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(startDate), 1),
        new Date(System.currentTimeMillis()), TimeUnit.DAYS).intValue();
    //Timber.e("fetchTrackingStats diff: " + diff);

    List<Stats> statsCached = mDbRepositoryStats.getAllEntities(diff + 1);
    for (Stats s : statsCached) {
      if (s.getStepsCount() == Constants.FAKE_STEPS_COUNT) {
        needToUpdateDataTracking = true;
      }
    }
    //Timber.e("fetchTrackingStats statsCached.size:" + statsCached.size());

    for (int i = statsCached.size(); i < 22; i++) {
      statsCached.add(new Stats(i, "", "", 0, 0));
    }
    Timber.e("fetchTrackingStats needToupdateData:" + needToUpdateDataTracking);

    if (!needToUpdateDataTracking) {
      getViewState().showTrackingStats(statsCached);
    } else {
      Subscription subscription = mDataManager.fetchTrackingStats(trackingId)
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
            getViewState().showTrackingStats(statsList);
          }, Throwable::printStackTrace);
      addToUnsubscription(subscription);
    }
  }

  /**
   * make query if even one stat is empty (has FAKE_STEPS_COUNT), and update all stats
   * Osobenno dlya MyStats
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

  public void fetchAverageStats(Tracking tracking) throws ParseException {
    StatsResponse statsResponse = mDataManager.getAverageStatsFromSHP();
    //if (statsResponse != null) getViewState().showStats(Arrays.asList(statsResponse.getResult()));
    String startDate = "none date";

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    long now = System.currentTimeMillis();

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(now);

    Timber.e("fetchAverageStats startDate = " + tracking.getCreatedAt());
    Timber.e("fetchAverageStats endDate = " + formatter.format(calendar.getTime()));

    Subscription subscription =
        mDataManager.fetchAverageStats(tracking.getId(), tracking.getCreatedAt(),
            formatter.format(calendar.getTime()))
            .filter(statsResponseResponse -> statsResponseResponse.body() != null)
            .filter(statsResponseResponse -> statsResponseResponse.body().getResult() != null)
            .filter(statsResponseResponse -> statsResponseResponse.body().getResult().length != 0)
            .flatMap(statsResponseResponse -> {
              Timber.e("fetchAverageStats " + statsResponseResponse.code());
              mDataManager.saveAverageStats(statsResponseResponse.body());
              return Observable.just(statsResponseResponse.body().getResult());
            })
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(stats -> {
              if (stats != null) {
                Timber.e("fetchAverageStats stats length " + stats.length);
              }
            }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public BarData generateBarData(List<Stats> stats, float coeficient) {
    ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

    for (int index = 0; index < stats.size() * 1; index++) {
      entries1.add(new BarEntry(index, 10000 * coeficient));
    }

    BarDataSet set1 = new BarDataSet(entries1, "10000 Steps");
    set1.setColor(Color.argb(20, 242, 197, 197));
    set1.setValueTextSize(0f); //make text invisible
    set1.setBarBorderColor(Color.argb(0, 242, 197, 197));
    set1.setAxisDependency(YAxis.AxisDependency.LEFT);

    BarData d = new BarData(set1);
    return d;
  }

  public LineData generateLineData(List<Stats> stats, List<Integer> statsSteps, Drawable drawable,
      boolean isAverageLineNeed, float coeficient) {
    entries = new ArrayList<>();
    entriesDashed = new ArrayList<Entry>();
    sempStat = new ArrayList<Stats>();
    //MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
    //mChart.setMarker(mv);
    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

    LineData d = new LineData();

    for (int index = 0; index < stats.size(); index++) {
      if (!stats.get(index).getLabel().trim().isEmpty()) {
        sempStat.add(stats.get(index));
      }
    }

    for (int i = 0; i < sempStat.size(); i++) {
      //      Timber.e("generateLineData " + stats.get(i).getLabel());
      entries.add(new Entry(i, sempStat.get(i).getStepsCount() * coeficient));
      //add icon to last point of chart
      if (i == sempStat.size() - 1) {
        entries.remove(sempStat.size() - 1);
        entries.add(new Entry(i, SharedPreferenceHelper.getReportStatsLocal(
            SharedPreferenceHelper.getUserId())[0].getStepsCount()
            * coeficient/*,drawable*/)); // add icon to point on chart
        Timber.e("SHARED HUY " + SharedPreferenceHelper.getUserTodaySteps());
      }
    }
    //setTodaySteps(SharedPreferenceHelper.getUserTodaySteps());
    //entries.remove(entries.size() - 1);
    //entries.add(new Entry(entries.size(), SharedPreferenceHelper.getUserTodaySteps()));
    //
    //getViewState().changeGraphData();

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
    dataSets.add(set);
    //set.setDrawFilled(true); // coloring under chart

    //Dashed stuff
    List<Stats> cashedStats = new ArrayList<>();

    if (isAverageLineNeed && mDataManager.getAverageStatsFromSHP() != null) {
      Timber.e("generateLineData cashedStats " + mDataManager.getAverageStatsFromSHP()
          .getResult().length);
      cashedStats.addAll(Arrays.asList(mDataManager.getAverageStatsFromSHP().getResult()));

      for (int index = 0; index < cashedStats.size(); index++) {
        entriesDashed.add(new Entry(index, cashedStats.get(index).getStepsCount() * coeficient));
      }

      LineDataSet setDashed = new LineDataSet(entriesDashed, "Group Steps");
      setDashed.setColor(Color.rgb(231, 145, 129));
      setDashed.setLineWidth(2.5f);
      setDashed.setValueTextSize(0f);
      setDashed.enableDashedLine(10, 10, 0);
      setDashed.setAxisDependency(YAxis.AxisDependency.LEFT);
      d.addDataSet(setDashed);
      dataSets.add(setDashed);
    }

    return new LineData(dataSets);
  }

  public void setTodaySteps(int steps) {
    entries.remove(entries.size() - 1);
    entries.add(new Entry(entries.size(), steps));

    getViewState().changeGraphData();
  }

  private List<String> getUnSeenMessages(List<Message> allMessages, String userID) {
    List<String> unSeenMessagesIds = new ArrayList<>();
    for (Message item : allMessages) {
      boolean seen = false;
      if (userID.equals(item.getUser().getUserID())) {
        seen = true;
      } else {
        for (SeenBy itemUser : item.getSeenBy()) {
          if (itemUser.getUser().getUserID().equals(userID)) {
            seen = true;
            continue;
          }
        }
      }
      if (!seen) {
        unSeenMessagesIds.add(item.getId());
      }
    }
    return unSeenMessagesIds;
  }

  //STEPS
  public int findMaxSteps(List<Stats> stats) {
    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < stats.size(); i++) {
      integers.add(stats.get(i).getStepsCount());
    }
    if (integers.size() == 0) return 0;
    return Collections.max(integers);
  }

  public int findMinSteps(List<Stats> stats) {

    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < stats.size(); i++) {
      Timber.e("AVG Steps " + stats.get(i).getStepsCount());
      if (stats.get(i).getStepsCount() != -1 && stats.get(i).getStepsCount() != 0) {
        integers.add(stats.get(i).getStepsCount());
      }
    }
    if (integers.isEmpty()) return 0;
    return Collections.min(integers);
  }

  public float findAverageSteps(List<Stats> stats) {
    Integer integer = 0;
    Integer countNotFake = 0;
    for (int i = 0; i < stats.size(); i++) {
      if (stats.get(i).getStepsCount() != -1) {
        integer += stats.get(i).getStepsCount();
        countNotFake++;
      }
    }
    //return findMinSteps(stats) + findMaxSteps(stats) / 2;
    return integer / countNotFake;
  }

  public int findDaysAboveAverageSteps(List<Stats> stats) {
    int avgCount = 0;
    float avg = findAverageSteps(stats);
    for (Stats s : stats) {
      if (s.getStepsCount() > avg) avgCount++;
    }
    return avgCount;
  }
  //KM

  public float findMaxKm(List<Stats> stats) {
    return findMaxSteps(stats) * KM_COEFFICIENT;
  }

  public float findMinKm(List<Stats> stats) {
    return (findMinSteps(stats) * KM_COEFFICIENT);
  }

  public float findAverageKm(List<Stats> stats) {
    return (findAverageSteps(stats) * KM_COEFFICIENT);
  }

  public int findDaysAboveAverageKm(List<Stats> stats) {
    int avgCount = 0;
    float avg = findAverageSteps(stats) * KM_COEFFICIENT;
    for (Stats s : stats) {
      if (s.getStepsCount() * KM_COEFFICIENT > avg) avgCount++;
    }
    return avgCount;
  }
  //KCAL

  public float findMaxKcal(List<Stats> stats) {
    return (findMaxSteps(stats) * KCAL_COEFFICIENT);
  }

  public float findMinKcal(List<Stats> stats) {
    return (findMinSteps(stats) * KCAL_COEFFICIENT);
  }

  public float findAverageKcal(List<Stats> stats) {
    return (findAverageSteps(stats) * KCAL_COEFFICIENT);
  }

  public int findDaysAboveAverageKcal(List<Stats> stats) {
    int avgCount = 0;
    float avg = findAverageSteps(stats) * KCAL_COEFFICIENT;
    for (Stats s : stats) {
      if (s.getStepsCount() * KCAL_COEFFICIENT > avg) avgCount++;
    }
    return avgCount;
  }

  public void fetchMessages(String roomId, String lastMessageId, String token) {
    Timber.e(" fetchMessages token: "
        + token
        + "   lastMessageId: "
        + lastMessageId
        + "  roomId: "
        + roomId);
    if (TextUtils.isEmpty(lastMessageId)) {
      lastMessageId = "0";
    }

    User user = new User();
    user.roomID = roomId; // ->  id of room
    user.userID = SharedPreferenceHelper.getUserId(); // ->  id of user
    user.name = SharedPreferenceHelper.getUserName(); // ->  name of user
    user.avatarURL = SharedPreferenceHelper.getAvatar();//  ->  user avatar, this is optional

    Timber.e("user :" + user.toString());

    String finalLastMessageId = lastMessageId;
    Subscription subscription = mDataManager.fetchMessagesSpika(roomId, finalLastMessageId, token)
        .filter(getMessagesModelRefactored -> (getMessagesModelRefactored != null
            && getMessagesModelRefactored.getData() != null))
        .concatMap(getMessagesModelRefactored -> {
          List<String> unReadMessagesIds =
              getUnSeenMessages(getMessagesModelRefactored.getData().getMessages(),
                  SharedPreferenceHelper.getUserId());
          return Observable.just(unReadMessagesIds);
        })
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(unReadMessagesIds -> {
          Timber.e(" fetchMessages" + "   unReadMessages: " + unReadMessagesIds.size());
          getViewState().setUnreadMessages(unReadMessagesIds.size());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
