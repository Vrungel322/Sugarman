package com.sugarman.myb.ui.activities.statsTracking;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.utils.DataUtils;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 01.03.2018.
 */
@InjectViewState public class StatsTrackingActivityPresenter
    extends BasicPresenter<IStatsTrackingActivityView> {
  @Inject DbRepositoryStats mDbRepositoryStats;
  private boolean needToUpdateData;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void fetchTrackingStats(String trackingId, Tracking tracking, boolean isMentors) throws ParseException {
    String startDate;
    String userJoinDate = "";
    if (!isMentors) {
       startDate = new SimpleDateFormat("yyyy-MM-dd").format(
          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(tracking.getStartDate()));
    }
    else {

      Member[] members = tracking.getMembers();

      for(Member m : members)
      {
        if(m.getId().equals(SharedPreferenceHelper.getUserId()))
        {
          userJoinDate = m.getCreatedAt();
        }
      }

      Timber.e("User join date " + userJoinDate);

      if(userJoinDate.isEmpty())
        userJoinDate = tracking.getStartDate();

      startDate = new SimpleDateFormat("yyyy-MM-dd").format(
          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(userJoinDate));
    }
    //Timber.e("fetchTrackingStats startDate: " + startDate);

    int diff = DataUtils.getDateDiff(DataUtils.subtractDays(
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(tracking.getStartDate()), 1),
        new Date(System.currentTimeMillis()), TimeUnit.DAYS).intValue();
    //Timber.e("fetchTrackingStats diff: " + diff);

    List<Stats> statsCached = mDbRepositoryStats.getAllEntities(diff + 1);
    for (Stats s : statsCached) {
      if (s.getStepsCount() == Constants.FAKE_STEPS_COUNT) {
        needToUpdateData = true;
      }
    }
    //Timber.e("fetchTrackingStats statsCached.size:" + statsCached.size());

    for (int i = statsCached.size(); i < 22; i++) {
      statsCached.add(new Stats(i, "", "", 0, 0));
    }
    //Timber.e("fetchTrackingStats needToupdateData:" + needToUpdateData);

    if (!needToUpdateData) {
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
          });
      addToUnsubscription(subscription);
    }
  }
}
