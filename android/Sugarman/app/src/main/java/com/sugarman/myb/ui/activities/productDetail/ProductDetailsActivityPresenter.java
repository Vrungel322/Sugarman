package com.sugarman.myb.ui.activities.productDetail;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;

/**
 * Created by nikita on 10.10.2017.
 */
@InjectViewState public class ProductDetailsActivityPresenter
    extends BasicPresenter<IProductDetailsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
  }

  public void checkNumberOfInviters() {
    Subscription subscription = mDataManager.countInvites()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(countInvitesResponse -> {
          if (Integer.valueOf(countInvitesResponse.getCount()) >= 5) {
            getViewState().startCheckoutActivityWithFreePrice();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
