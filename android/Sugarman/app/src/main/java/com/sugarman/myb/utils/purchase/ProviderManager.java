package com.sugarman.myb.utils.purchase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.sugarman.myb.BuildConfig;
import com.sugarman.myb.api.ApiRx;
import com.sugarman.myb.api.RestApi;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.iab.Subscriptions;
import com.sugarman.myb.models.mentor.MentorFreeSomeLayer;
import com.sugarman.myb.ui.activities.mentorDetail.GooglePurchaseListener;
import com.sugarman.myb.ui.activities.mentorDetail.MentorDetailActivity;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.inapp.IabHelper;
import com.sugarman.myb.utils.inapp.IabResult;
import com.sugarman.myb.utils.inapp.Inventory;
import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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

  public ProviderManager(Context context, RestApi restApi, IabHelper iabHelper) {
    mContext = context;
    mRestApi = restApi;
    //mHelper = iabHelper;
  }

  public Observable<Subscriptions> startFreePurchaseFlowByVendor(String vendor,
      String mentorId, String slot) {
    return new Retrofit.Builder().baseUrl(vendor)
        .client(new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(provideHttpLoggingInterceptor())
            .addInterceptor(provideHeaderInterceptor())
            .build())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(GsonConverterFactory.create(
            new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .serializeNulls()
                .create()))
        .build()
        .create(ApiRx.class)
        .purchaseMentorForFree(PurchaseTransaction.builder()//v1/get_free_subscription_data
            .mentorId(mentorId).freeSku(slot).build())
        .concatMap(mentorFreeResponceResponse -> Observable.just(mentorFreeResponceResponse.body()))
        .concatMap(mentorFreeResponce -> mRestApi.checkPurchaseTransaction(
            MentorFreeSomeLayer.builder()
                .receiptData(mentorFreeResponce.getReceiptData())
                .vendor(vendor)
                .freeSku(slot)
                .mentorId(mentorId)
                .build()))//v1/subscribe_for_mentor

        .concatMap(subscriptionsResponse -> Observable.just(subscriptionsResponse.body()));

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

  public void setupInAppPurchase(String slot, String mentorId, MentorDetailActivity activity,
      String vendor, GooglePurchaseListener googlePurchaseListener) {
    mHelper = new IabHelper(mContext, Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(result -> {
      if (!result.isSuccess()) {
        Timber.e("In-app Billing setup failed: " + result);
      } else {
        Timber.e("In-app Billing is set up OK");
        startGooglePurchaseFlowByVendor(slot, mentorId, activity, vendor, googlePurchaseListener);
      }
    });
    mHelper.enableDebugLogging(true);
  }

  public void startPurchaseFlow(String freeSku, MentorDetailActivity activity) {
    mFreeSku = freeSku;
    Timber.e("mFreeSku startPurchaseFlow " + freeSku);
    MentorDetailActivity.startPurchaseFlow(activity, mHelper, freeSku, mPurchaseFinishedListener,
        "mypurchasetoken");

    //mHelper.launchSubscriptionPurchaseFlow(activity, freeSku, 10001, mPurchaseFinishedListener,
    //    "mypurchasetoken");
  }

  public void consumeItem() {
    mHelper.queryInventoryAsync(true, Arrays.asList(mFreeSku), mReceivedInventoryListener);
  }

  public void clearListenersFreeObj() {
    mGooglePurchaseListener = null;
    if (mHelper != null) {
      mHelper.dispose();
    }
  }

  Interceptor provideHeaderInterceptor() {
    Timber.e("Got into interceptor");
    okhttp3.Interceptor requestInterceptor = new okhttp3.Interceptor() {
      @Override public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request;

        String token = SharedPreferenceHelper.getAccessToken();
        Log.e("APP", "Token = " + token);
        Request.Builder requestBuilder = original.newBuilder();
        // .header("content-type", "application/json");

        if (!TextUtils.isEmpty(token)) {
          requestBuilder.header(Constants.AUTHORIZATION, Constants.BEARER + token);
          requestBuilder.header(Constants.TIMEZONE, TimeZone.getDefault().getID());
          requestBuilder.header(Constants.TIMESTAMP, System.currentTimeMillis() + "");
          requestBuilder.header(Constants.VERSION, DeviceHelper.getAppVersionName());
          requestBuilder.header(Constants.IMEI, SharedPreferenceHelper.getIMEI());
        }

        request = requestBuilder.build();
        Response response = chain.proceed(request);
        return response;
      }
    };
    return requestInterceptor;
  }

  HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    HttpLoggingInterceptor interceptor =
        new HttpLoggingInterceptor(message -> Timber.tag("response").d(message));
    interceptor.setLevel(
        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY);
    return interceptor;
  }
}