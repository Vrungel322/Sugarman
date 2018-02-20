package com.sugarman.myb.utils.purchase;

import android.content.Context;
import com.sugarman.myb.api.ApiRx;
import com.sugarman.myb.api.RestApi;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.ui.activities.mentorDetail.GooglePurchaseListener;
import com.sugarman.myb.ui.activities.mentorDetail.MentorDetailActivity;
import com.sugarman.myb.utils.inapp.IabHelper;
import com.sugarman.myb.utils.inapp.IabResult;
import com.sugarman.myb.utils.inapp.Inventory;
import java.util.Arrays;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by nikita on 16.02.2018.
 */

public class ProviderManager {
  public static final String FREE = "free";
  public static final String GOOGLE = "google";
  private final Context mContext;
  private final RestApi mRestApi;
  IabHelper mHelper;
  private GooglePurchaseListener mGooglePurchaseListener;
  private String mFreeSku;
  IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = (result, purchase) -> {
    Timber.e("mFreeSku mPurchaseFinishedListener " + mFreeSku);

    if (result.isFailure()) {
      // Handle error
      return;
    } else if (purchase.getSku().equals(mFreeSku)) {
      consumeItem();
      Timber.e(mHelper.getMDataSignature());
    } else {
      Timber.e(result.getMessage());
    }
  };
  private String mMentorId;
  private String mVendor;
  IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener =
      new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
          Timber.e("mFreeSku mReceivedInventoryListener " + mFreeSku);

          if (result.isFailure()) {
            // Handle failure
          } else {
            mHelper.consumeAsync(inventory.getPurchase(mFreeSku), (purchase, result1) -> {
              Timber.e("mConsumeFinishedListener" + purchase.toString());
              Timber.e("mConsumeFinishedListener" + result.toString());
            });
            //mHelper.consumeAsync(inventory.getAllPurchases(), mOnConsumeMultiFinishedListener);
            Timber.e(result.getMessage());
            Timber.e(inventory.getSkuDetails(mFreeSku).getTitle());
            Timber.e(inventory.getSkuDetails(mFreeSku).getSku());

            mGooglePurchaseListener.action(PurchaseTransaction.builder()
                .mentorId(mMentorId)
                .vendor(mVendor)
                .freeSku(mFreeSku)
                .purchaseToken(inventory.getPurchase(mFreeSku).getToken())
                .build());
          }
        }
      };

  public ProviderManager(Context context, RestApi restApi, IabHelper iabHelper) {
    mContext = context;
    mRestApi = restApi;
    //mHelper = iabHelper;
  }

  public Observable<PurchaseTransaction> startFreePurchaseFlowByVendor(String vendor,
      String mentorId, String slot) {
    return new Retrofit.Builder().baseUrl(vendor)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiRx.class)
        .checkPurchaseTransaction(
            PurchaseTransaction.builder().vendor(vendor).mentorId(mentorId).freeSku(slot).build())
        .concatMap(voidResponse -> Observable.just(
            PurchaseTransaction.builder().vendor(vendor).mentorId(mentorId).freeSku(slot).build()));

    //return mRestApi.purchaseMentorForFree(mentorId)
    //    .concatMap(responseObservable -> Observable.just(PurchaseTransaction.builder()
    //        .tracking(responseObservable.body().getTracking())
    //        .build()));
  }

  public void startGooglePurchaseFlowByVendor(String slot, String mentorId,
      MentorDetailActivity activity, String vendor, GooglePurchaseListener googlePurchaseListener) {
    Timber.e("startGooglePurchaseFlowByVendor slot:" + slot + " mentorId: " + mentorId);
    mMentorId = mentorId;
    mVendor = vendor;
    mGooglePurchaseListener = googlePurchaseListener;
    //mRestApi.getNextFreeSku().concatMap(nextFreeSkuEntityResponse -> {
    startPurchaseFlow(slot, activity);
    //return Observable.empty();
    //}).subscribe();
  }

  public void setupInAppPurchase(String slot, String mentorId,
      MentorDetailActivity activity, String vendor, GooglePurchaseListener googlePurchaseListener) {
    mHelper = new IabHelper(mContext, Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(result -> {
      if (!result.isSuccess()) {
        Timber.e("In-app Billing setup failed: " + result);
      } else {
        Timber.e("In-app Billing is set up OK");
        startGooglePurchaseFlowByVendor(slot,mentorId,activity,vendor,googlePurchaseListener);
      }
    });
    mHelper.enableDebugLogging(true);
  }

  public void startPurchaseFlow(String freeSku, MentorDetailActivity activity) {
    mFreeSku = freeSku;
    Timber.e("mFreeSku startPurchaseFlow " + freeSku);

    mHelper.launchSubscriptionPurchaseFlow(activity, freeSku, 10001, mPurchaseFinishedListener,
        "mypurchasetoken");
  }

  public void consumeItem() {
    mHelper.queryInventoryAsync(true, Arrays.asList(mFreeSku), mReceivedInventoryListener);
  }

  public void clearListenersFreeObj() {
    mGooglePurchaseListener = null;
    mHelper.dispose();
  }
}