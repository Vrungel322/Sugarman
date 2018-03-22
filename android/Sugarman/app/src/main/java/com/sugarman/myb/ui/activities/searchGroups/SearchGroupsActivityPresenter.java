package com.sugarman.myb.ui.activities.searchGroups;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.trackings.TrackingsResponse;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.data.db.DbRepositorySearchTrackings;
import com.sugarman.myb.utils.ThreadSchedulers;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 22.03.2018.
 */
@InjectViewState public class SearchGroupsActivityPresenter
    extends BasicPresenter<ISearchGroupsActivityView> {
  @Inject DbRepositorySearchTrackings mDbRepositorySearchTrackings;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void fetchTrackings(String availableType, String query) {
    TrackingsResponse cachedResponce = mDbRepositorySearchTrackings.getAllEntities();
    if (cachedResponce != null
        && mDbRepositorySearchTrackings.getAllEntities().getResult() != null) {
      getViewState().showTrackings(mDbRepositorySearchTrackings.getAllEntities().getResult(),
          availableType);
    }
    Subscription subscription = mDataManager.getTrackings(query, availableType)
        .compose(ThreadSchedulers.applySchedulers())
        .flatMap(trackingsResponseResponse -> {
          Timber.e("fetchTrackings code: "
              + trackingsResponseResponse.code()
              + " size: "
              + trackingsResponseResponse.body().getResult().size());
          mDbRepositorySearchTrackings.saveEntity(trackingsResponseResponse.body());

          return Observable.just(trackingsResponseResponse.body());
        })
        .subscribe(trackingsResponse -> getViewState().showTrackings(trackingsResponse.getResult(),
            availableType));
    addToUnsubscription(subscription);
  }
}
