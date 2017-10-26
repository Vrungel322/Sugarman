package com.sugarman.myb.ui.activities.shop;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import java.util.List;

/**
 * Created by nikita on 10.10.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IShopActivityView
    extends MvpView {
  void showProducts(List<ShopProductEntity> shopProductEntities);
}
