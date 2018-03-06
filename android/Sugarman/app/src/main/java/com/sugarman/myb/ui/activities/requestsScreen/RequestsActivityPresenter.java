package com.sugarman.myb.ui.activities.requestsScreen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 06.03.2018.
 */
@InjectViewState public class RequestsActivityPresenter
    extends BasicPresenter<IRequestsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
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
