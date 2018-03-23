package com.sugarman.myb.ui.activities.requestsScreen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbRepositoryRequests;
import com.sugarman.myb.utils.ThreadSchedulers;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 06.03.2018.
 */
@InjectViewState public class RequestsActivityPresenter
    extends BasicPresenter<IRequestsActivityView> {
  @Inject DbRepositoryRequests mDbRepositoryRequests;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchRequests();
  }

  private void fetchRequests() {
    getViewState().showRequests(mDbRepositoryRequests.getAllEntities());
    Subscription subscription = mDataManager.getMyRequests()
        .concatMap(requestsResponseResponse -> Observable.from(
            requestsResponseResponse.body().getResult()))
        .filter(request -> request.getTracking().getStatus() != Constants.STATUS_FAILED)
        .filter(request -> request.getTracking().getStatus() != Constants.STATUS_COMPLETED)
        .toList()
        .filter(requests -> requests != null)
        .flatMap(requests -> {
          mDbRepositoryRequests.saveEntity(requests);
          return Observable.just(requests);
        })
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(requests -> {
          Timber.e("fetchRequests size: " + requests.size());
          getViewState().showRequests(requests);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void declineRequest(String requestId) {
    Subscription subscription = mDataManager.declineRequest(requestId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("declineRequest code: " + objectResponse.code());
          if (objectResponse.code() >= 200 && objectResponse.code() < 300) {
            getViewState().declineRequestAction();
          } else {
            getViewState().errorMsg(objectResponse.errorBody().toString());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void acceptRequest(String requestId) {
    Subscription subscription = mDataManager.acceptRequest(requestId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(objectResponse -> {
          Timber.e("acceptRequest code: " + objectResponse.code());
          if (objectResponse.code() >= 200 && objectResponse.code() < 300) {
            getViewState().acceptRequestAction();
          } else {
            getViewState().errorMsg(objectResponse.errorBody().toString());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
