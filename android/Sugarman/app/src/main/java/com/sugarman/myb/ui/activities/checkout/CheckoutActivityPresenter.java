package com.sugarman.myb.ui.activities.checkout;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;

/**
 * Created by nikita on 25.09.17.
 */
@InjectViewState public class CheckoutActivityPresenter
    extends BasicPresenter<ICheckoutActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void sendPurchaseData(String countryName, String cityName, String streetName,
      String zipCode, String fullName, String phoneNumber, int amountPrice, int num,
      String productName) {
    Subscription subscription =
        mDataManager.sendPurchaseData(countryName, cityName, streetName, zipCode, fullName,
            phoneNumber, String.valueOf(amountPrice), String.valueOf(num), productName)
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(voidResponse -> {
              if (voidResponse.code() == Constants.SUCCESS_RESPONSE_CODE){
                getViewState().finishCheckoutActivity();
              }
            },Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
