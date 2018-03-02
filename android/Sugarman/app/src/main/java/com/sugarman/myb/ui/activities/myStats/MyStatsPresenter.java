package com.sugarman.myb.ui.activities.myStats;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.utils.ThreadSchedulers;
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
            getViewState().showStats(statsList);
          }, Throwable::printStackTrace);
      addToUnsubscription(subscription);
    }
  }

  public void fetchStatByDate(String date) {
    Subscription subscription = mDataManager.fetchStatByDate(date)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(statsResponse -> {
          Timber.e("fetchStatByDate" + statsResponse.code());
        });
    addToUnsubscription(subscription);
  }
}
