package com.sugarman.myb.ui.activities.searchGroups;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 22.03.2018.
 */
@InjectViewState public class SearchGroupsActivityPresenter
    extends BasicPresenter<ISearchGroupsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void fetchTrackings(String availableType, String query) {
    Subscription subscription = mDataManager.getTrackings(query, availableType)
        .compose(ThreadSchedulers.applySchedulers())
        .flatMap(trackingsResponseResponse -> {
          Timber.e("fetchTrackings code: "+ trackingsResponseResponse.code() + " size: " + trackingsResponseResponse.body().getResult().length);
          return Observable.just(trackingsResponseResponse.body());
        })
        .subscribe(trackingsResponse -> getViewState().showTrackings(trackingsResponse.getResult(), availableType));
    addToUnsubscription(subscription);
  }
}
