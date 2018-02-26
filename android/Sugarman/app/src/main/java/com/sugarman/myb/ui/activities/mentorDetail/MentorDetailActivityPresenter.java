package com.sugarman.myb.ui.activities.mentorDetail;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.models.iab.Subscriptions;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.inapp.Purchase;
import com.sugarman.myb.utils.purchase.ProviderManager;
import com.sugarman.myb.utils.purchase.PurchaseTransaction;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 10/30/17.
 */
@InjectViewState public class MentorDetailActivityPresenter
    extends BasicPresenter<IMentorDetailActivityView> {
  @Inject ProviderManager mProviderManager;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    Timber.e("onFirstViewAttach");

    getViewState().setUpUI();
    getViewState().fillMentorsFriendsList();
    getViewState().fillMentorsVideosList();
  }

  public void fetchComments(String mentorId) {
    Subscription subscription = mDataManager.fetchComments(mentorId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(mentorsCommentsStupidAbstraction -> {
          getViewState().fillCommentsList(
              mentorsCommentsStupidAbstraction.getMMentorsCommentsEntities());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
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
          SharedPreferenceHelper.saveListSubscriptionEntity(
              subscriptionsResponse.body().getSubscriptionEntities());
          Timber.e(String.valueOf(subscriptionsResponse.code()));
          if (subscriptionsResponse.code() == 200) {
            //getViewState().moveToMainActivity();
            Tracking tracking = subscriptionsResponse.body().getTracking();
            if (tracking != null) {
              Timber.e("checkInAppBilling tracking != null");
              getViewState().moveToMentorsDetailActivity(tracking);
            } else {
              Timber.e("checkInAppBilling tracking == null");
            }
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void getMentorsVendor(String mentorId, MentorDetailActivity activity) {
    getViewState().showProgress();
    Subscription subscription = mDataManager.getMentorsVendor(mentorId) //v1/get_provider_data
        .concatMap(mentorsVendorResponse -> {
          Observable<Subscriptions> observable = Observable.empty();
          if (mentorsVendorResponse.body().getSlot().equals(ProviderManager.FREE)) {
            Timber.e("getMentorsVendor free " + mentorsVendorResponse.body().toString());
            observable = mProviderManager.startFreePurchaseFlowByVendor(
                mentorsVendorResponse.body().getVendor(), mentorId,
                mentorsVendorResponse.body().getSlot());
          }
          if (mentorsVendorResponse.body().getVendor().equals(ProviderManager.GOOGLE)) {
            if (mentorsVendorResponse.body().getIsAvailable()) {
              Timber.e(
                  "getMentorsVendor google vendor: " + mentorsVendorResponse.body().toString());
              mProviderManager.setupInAppPurchase(mentorsVendorResponse.body().getSlot(), mentorId,
                  activity, mentorsVendorResponse.body().getVendor(), purchaseTransaction -> {
                    getViewState().checkGPurchase(purchaseTransaction);
                  });
            } else {
              getViewState().slotUnavailableDialog();
            }
          }
          return observable;
        })
        //.filter(
        //    purchaseTransaction -> !purchaseTransaction.getFreeSku().equals(ProviderManager.FREE))

        //.concatMap(purchaseTransaction -> {
        //  Timber.e("getMentorsVendor purchaseTransaction: " + purchaseTransaction.toString());
        //  return mDataManager.checkPurchaseTransaction(purchaseTransaction);
        //})
        .compose(ThreadSchedulers.applySchedulers()).subscribe(subscriptions -> {
          getViewState().hideProgress();
          Timber.e("getMentorsVendor subscribe");
          getViewState().moveToMentorsDetailActivity(subscriptions.getTracking());

          mProviderManager.clearListenersFreeObj();
          Timber.e("getMentorsVendor Need to do smth on UI if checkPurchaseTransaction is OK");
          getViewState().enableButtons();
        }, throwable -> {
          getViewState().hideProgress();
          getViewState().tryAgainLaterDialog();
          Timber.e("getMentorsVendor throwable");
          mProviderManager.clearListenersFreeObj();
          throwable.printStackTrace();
        });
    addToUnsubscription(subscription);

    //Subscription subscription = mDataManager.getNextFreeSku()
    //    .compose(ThreadSchedulers.applySchedulers())
    //    .subscribe(nextFreeSkuEntityResponse -> {
    //      if (nextFreeSkuEntityResponse.code() == Constants.ALL_SLOTS_NOT_EMPTY_ERROR) {
    //        getViewState().showAllSlotsNotEmptyDialog();
    //      }
    //      if (nextFreeSkuEntityResponse.body().getFreeSku() != null
    //          && !nextFreeSkuEntityResponse.body().getFreeSku().isEmpty()) {
    //        getViewState().startPurchaseFlow(nextFreeSkuEntityResponse.body().getFreeSku());
    //      }
    //    }, Throwable::printStackTrace);
    //addToUnsubscription(subscription);
  }

  public void checkGPurchase(PurchaseTransaction purchaseTransaction) {
    Subscription subscription = mDataManager.checkPurchaseTransaction(purchaseTransaction)
        .concatMap(subscriptionsResponse -> Observable.just(subscriptionsResponse.body()))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(subscriptions -> getViewState().moveToMentorsDetailActivity(
            subscriptions.getTracking()), Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  //public void purchaseMentorForFree(String mentorId) {
  //  Timber.e("purchaseMentorForFree mentorId: " + mentorId);
  //  Subscription subscription = mDataManager.purchaseMentorForFree(mentorId)
  //      .compose(ThreadSchedulers.applySchedulers())
  //      .subscribe(voidResponse -> {
  //        Timber.e("purchaseMentorForFree code: " + voidResponse.code());
  //      }, Throwable::printStackTrace);
  //  addToUnsubscription(subscription);
  //}
}
