package com.sugarman.myb.ui.dialogs.dialogRescueBoldMan;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.iab.InAppSinglePurchase;
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

  public void checkInAppBillingOneDollar(String trackingId,Purchase purchase, String productName, String freeSku) {

    Subscription subscription = mDataManager.checkInAppBillingOneDollar(trackingId,
        new InAppSinglePurchase(productName, purchase.getSku(), purchase.getToken(), freeSku))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(subscriptionsResponse -> {

          Timber.e(String.valueOf(subscriptionsResponse.code()));
          if (subscriptionsResponse.code() == 200) {
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
