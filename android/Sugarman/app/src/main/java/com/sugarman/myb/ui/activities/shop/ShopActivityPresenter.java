package com.sugarman.myb.ui.activities.shop;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;

/**
 * Created by nikita on 10.10.2017.
 */
@InjectViewState public class ShopActivityPresenter extends BasicPresenter<IShopActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchProducts();
  }

  private void fetchProducts() {
    Subscription subscription = mDataManager.fetchProducts()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(shopProductEntities -> {
          getViewState().showProducts(shopProductEntities);
        });
    addToUnsubscription(subscription);
  }
}
