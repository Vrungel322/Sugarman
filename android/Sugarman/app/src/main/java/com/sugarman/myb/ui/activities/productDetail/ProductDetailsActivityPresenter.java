package com.sugarman.myb.ui.activities.productDetail;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

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

}
