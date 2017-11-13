package com.sugarman.myb.ui.activities.mentorDetail;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.iab.InAppBilling;
import com.sugarman.myb.models.iab.PurchaseForServer;
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

  public void checkInAppBilling(Purchase purchase, String signature) {
    Subscription subscription = mDataManager.checkInAppBilling(new InAppBilling(
        new PurchaseForServer(purchase.getOrderId(), purchase.getPackageName(), purchase.getSku(),
            purchase.getPurchaseTime(), purchase.getPurchaseState(), purchase.getDeveloperPayload(),
            purchase.getToken()), signature))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(voidResponse -> {
          Timber.e(String.valueOf(voidResponse.code()));
          if (voidResponse.code() == 200) {
            getViewState().moveToMainActivity();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
