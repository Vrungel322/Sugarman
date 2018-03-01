package com.sugarman.myb.ui.activities.myStats;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.data.db.DbRepositoryStats;
import com.sugarman.myb.utils.ThreadSchedulers;
import javax.inject.Inject;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 2/28/18.
 */
@InjectViewState public class MyStatsPresenter extends BasicPresenter<IMyStatsActivityView> {
  @Inject DbRepositoryStats mDbRepositoryStats;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    getViewState().showStats(mDbRepositoryStats.getAllEntities());
  }

  public void fetchStats() {
    Timber.e("fetchStats");
    Subscription subscription = mDataManager.fetchStats()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(statsResponseResponse -> {
          Timber.e("fetchStats code: " + statsResponseResponse.code());
          mDbRepositoryStats.saveEntityList(statsResponseResponse.body());
          getViewState().showStats(statsResponseResponse.body());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
