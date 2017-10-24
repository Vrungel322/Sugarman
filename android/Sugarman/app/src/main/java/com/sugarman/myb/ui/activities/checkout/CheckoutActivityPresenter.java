package com.sugarman.myb.ui.activities.checkout;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 25.09.17.
 */
@InjectViewState public class CheckoutActivityPresenter
    extends BasicPresenter<ICheckoutActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void sendPurchaseData(String countryName, String cityName, String streetName,
      String zipCode, String fullName, String phoneNumber, String amountPrice, int num,
      String productName) {
    Timber.e(amountPrice);
    Subscription subscription =
        mDataManager.sendPurchaseData(countryName, cityName, streetName, zipCode, fullName,
            phoneNumber, amountPrice, String.valueOf(num), productName)
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(voidResponse -> {
              Timber.e("" + voidResponse.code());
              if (voidResponse.code() == Constants.SUCCESS_RESPONSE_CODE){
                getViewState().startPayPalTransaction(amountPrice);
              }
            },Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
