package com.sugarman.myb.utils.purchase;

import android.content.Context;
import com.sugarman.myb.api.RestApi;
import rx.Observable;

/**
 * Created by nikita on 16.02.2018.
 */

public class ProviderManager {
  private final Context mContext;
  private final RestApi mRestApi;

  public ProviderManager(Context context, RestApi restApi) {
    mContext = context;
    mRestApi = restApi;
  }

  public Observable<PurchaseTransaction> startPurchaseFlowByVendor(String vendor, String mentorId) {

    switch (vendor) {
      case "free": {
        return mRestApi.purchaseMentorForFree(mentorId)
            .concatMap(responseObservable -> Observable.just(PurchaseTransaction.builder()
                .tracking(responseObservable.body().getTracking())
                .build()));
      }
      case "googlePlay": {

        return Observable.just(new PurchaseTransaction());
      }
    }
    return Observable.just(new PurchaseTransaction());
  }
}