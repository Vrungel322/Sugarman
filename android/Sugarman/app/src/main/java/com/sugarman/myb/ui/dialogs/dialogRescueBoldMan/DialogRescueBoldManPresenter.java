package com.sugarman.myb.ui.dialogs.dialogRescueBoldMan;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.iab.InAppSinglePurchase;
import com.sugarman.myb.utils.RxBusHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.inapp.Purchase;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 11.12.2017.
 */
@InjectViewState public class DialogRescueBoldManPresenter
    extends BasicPresenter<IDialogRescueBoldManView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    subscribeEventAboutInAppPurchaseFromMAinActivityOnActivityResult();
  }

  private void subscribeEventAboutInAppPurchaseFromMAinActivityOnActivityResult() {
    Subscription subscription = mRxBus.filteredObservable(RxBusHelper.EventAboutInAppPurchase.class)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(eventAboutInAppPurchase -> getViewState().handleActivityResult(
            eventAboutInAppPurchase.requestCode, eventAboutInAppPurchase.resultCode,
            eventAboutInAppPurchase.data), Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void checkInAppBillingOneDollar(String trackingId, Purchase purchase, String productName,
      String freeSku) {
    Timber.e("checkInAppBillingOneDollar");

    Subscription subscription = mDataManager.checkInAppBillingOneDollar(trackingId,
        new InAppSinglePurchase(productName, purchase.getSku(), purchase.getToken(), freeSku))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(subscriptionsResponse -> {
          getViewState().enableButton();

          Timber.e("checkInAppBillingOneDollar " + String.valueOf(subscriptionsResponse.code()));
          if (subscriptionsResponse.code() == 200) {
            getViewState().showCongratulationsDialog();
            getViewState().hideProgressBar();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
