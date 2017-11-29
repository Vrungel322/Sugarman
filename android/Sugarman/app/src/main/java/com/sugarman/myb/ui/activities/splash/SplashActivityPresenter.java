package com.sugarman.myb.ui.activities.splash;

import com.arellomobile.mvp.InjectViewState;
import com.google.gson.Gson;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.inapp.Purchase;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 21.11.2017.
 */
@InjectViewState public class SplashActivityPresenter extends BasicPresenter<ISplashActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void checkInAppBilling(Purchase purchase, String productName, String userId,
      String freeSku) {
    Timber.e("checkInAppBilling productName" + productName);
    Timber.e("checkInAppBilling getSku" + purchase.getSku());
    Timber.e("checkInAppBilling getToken" + purchase.getToken());
    Timber.e("checkInAppBilling userId" + userId);
    Timber.e("checkInAppBilling freeSku" + freeSku);
    Subscription subscription = mDataManager.checkInAppBilling(
        new PurchaseForServer(productName, purchase.getSku(), purchase.getToken(), userId, freeSku))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(subscriptionsResponse -> {
          Timber.e(String.valueOf(subscriptionsResponse.code()));
          Timber.e("save json " + String.valueOf(
              new Gson().toJson(subscriptionsResponse.body().getSubscriptionEntities())));
          SharedPreferenceHelper.saveListSubscriptionEntity(
              subscriptionsResponse.body().getSubscriptionEntities());
          if (subscriptionsResponse.code() == 229) {
            // no money no honey
          }
          if (subscriptionsResponse.code() == 200) {
            //  getViewState().moveToMainActivity();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
