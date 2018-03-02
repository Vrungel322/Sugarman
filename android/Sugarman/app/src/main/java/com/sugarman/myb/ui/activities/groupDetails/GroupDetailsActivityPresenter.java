package com.sugarman.myb.ui.activities.groupDetails;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.inapp.Purchase;
import rx.Subscription;

/**
 * Created by nikita on 03.11.2017.
 */
@InjectViewState public class GroupDetailsActivityPresenter
    extends BasicPresenter<IGroupDetailsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void sendComment(String mentorsId, float rating, String commentBody) {
    Subscription subscription =
        mDataManager.sendComment(mentorsId, String.valueOf(rating), commentBody)
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(voidResponse -> {
              if (voidResponse.code() == 200) {
                getViewState().closeDialog();
              }
            }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  //@Deprecated
  //public void cancelSubscription(Purchase purchase, String productName, String groupOwnerId,
  //    String slot) {
  //  Timber.e("checkInAppBilling productName" + productName);
  //  Timber.e("checkInAppBilling getSku" + purchase.getSku());
  //  Timber.e("checkInAppBilling getToken" + purchase.getToken());
  //  Timber.e("checkInAppBilling userId" + groupOwnerId);
  //  Timber.e("checkInAppBilling freeSku" + slot);
  //  Subscription subscription = mDataManager.closeSubscription(
  //      new PurchaseForServer(productName, purchase.getSku(), purchase.getToken(), groupOwnerId,
  //          slot)).compose(ThreadSchedulers.applySchedulers()).subscribe(subscriptionsResponse -> {
  //    Timber.e("cancelSubscription code " + subscriptionsResponse.code());
  //    Timber.e("save json " + String.valueOf(
  //        new Gson().toJson(subscriptionsResponse.body().getSubscriptionEntities())));
  //    SharedPreferenceHelper.saveListSubscriptionEntity(
  //        subscriptionsResponse.body().getSubscriptionEntities());
  //
  //    if (subscriptionsResponse.code() == 200) {
  //      getViewState().moveToMainActivity();
  //    }
  //  }, Throwable::printStackTrace);
  //  addToUnsubscription(subscription);
  //}

  public void getSlotToUnsubscribe(String mentorId) {
    Subscription subscription = mDataManager.getMentorsVendor(mentorId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(mentorsVendorResponse -> {
          getViewState().unsubscribeMEntor(mentorsVendorResponse.body().getSlot());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void cancelSubscription(String mentorId, Purchase purchase) {
    Subscription subscription =
        mDataManager.closeSubscription(mentorId, purchase == null ? "" : purchase.getToken())
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(voidResponse -> {
              if (voidResponse.code() == 200) {
                getViewState().moveToMainActivity();
              }
            }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
