package com.sugarman.myb.ui.activities.myStats;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 2/28/18.
 */
@InjectViewState public class MyStatsPresenter extends BasicPresenter<IMyStatsActivityView> {
  @Inject DbRepositoryStats mDbRepositoryStats;
  private boolean needToupdateData;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    Timber.e("onFirstViewAttach");
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

  /**
   * make query only for that stats that are empty (has FAKE_STEPS_COUNT),save the missing stats to
   * SHP,and add this stats to existing stats, and send them to subscribe
   */
  public void fetchStatByDate() {
    List<Stats> needToUpdateStat = new ArrayList<>();
    List<Stats> noNeedToUpdateStat = new ArrayList<>();
    List<Stats> statsCached = mDbRepositoryStats.getAllEntities(21);
    for (Stats s : statsCached) {
      if (s.getStepsCount() == Constants.FAKE_STEPS_COUNT) {
        needToUpdateStat.add(s);
      }
      if (s.getStepsCount() != Constants.FAKE_STEPS_COUNT) {
        noNeedToUpdateStat.add(s);
      }
    }

    Subscription subscription = Observable.from(needToUpdateStat)
        .concatMap(stats -> mDataManager.fetchStatByDate(stats.getDate()))
        .filter(statsResponse -> statsResponse.body() != null)
        .concatMap(statsResponse -> {
          mDbRepositoryStats.saveEntity(statsResponse.body());
          return Observable.just(statsResponse.body());
        })
        .toList()
        .concatMap(statsList -> {
          noNeedToUpdateStat.addAll(statsList);
          return Observable.just(needToUpdateStat);
        })
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(stats -> {
          Timber.e("fetchStatByDate stats.size = " + stats.size());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);

    //Subscription subscription = mDataManager.fetchStatByDate(date)
    //    .filter(statsResponse -> statsResponse.body() != null)
    //    .concatMap(statsResponse -> {
    //      mDbRepositoryStats.saveEntity(statsResponse.body());
    //      return Observable.just(statsResponse.body());
    //    })
    //    .compose(ThreadSchedulers.applySchedulers())
    //    .subscribe(stats -> {
    //      Timber.e("fetchStatByDate" + stats);
    //    });
    //addToUnsubscription(subscription);
  }
}
