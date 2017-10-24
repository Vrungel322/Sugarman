package com.sugarman.myb.ui.activities.shop;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.List;
import rx.Subscription;

/**
 * Created by nikita on 10.10.2017.
 */
@InjectViewState public class ShopActivityPresenter extends BasicPresenter<IShopActivityView> {
  private final List<String> mProductNameList;

  public ShopActivityPresenter(List<String> productNames) {
    this.mProductNameList = productNames;
  }

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchProducts();
  }

  private void fetchProducts() {
    Subscription subscription = mDataManager.fetchProducts(mProductNameList)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(shopProductEntities -> {
          getViewState().showProducts(shopProductEntities);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
