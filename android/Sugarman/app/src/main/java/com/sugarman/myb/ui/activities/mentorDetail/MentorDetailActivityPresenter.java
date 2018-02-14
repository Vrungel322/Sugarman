package com.sugarman.myb.ui.activities.mentorDetail;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.inapp.Purchase;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 10/30/17.
 */
@InjectViewState public class MentorDetailActivityPresenter
    extends BasicPresenter<IMentorDetailActivityView> {
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

  public void getNextFreeSku() {
    Subscription subscription = mDataManager.getNextFreeSku()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(nextFreeSkuEntityResponse -> {
          if (nextFreeSkuEntityResponse.code() == Constants.ALL_SLOTS_NOT_EMPTY_ERROR) {
            getViewState().showAllSlotsNotEmptyDialog();
          }
          if (nextFreeSkuEntityResponse.body().getFreeSku() != null
              && !nextFreeSkuEntityResponse.body().getFreeSku().isEmpty()) {
            getViewState().startPurchaseFlow(nextFreeSkuEntityResponse.body().getFreeSku());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void purchaseMentorForFree(String mentorId) {
    Timber.e("purchaseMentorForFree mentorId: " + mentorId);
    Subscription subscription = mDataManager.purchaseMentorForFree(mentorId)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(voidResponse -> {
          Timber.e("purchaseMentorForFree code: " + voidResponse.code());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
