package com.sugarman.myb.ui.activities.productDetail;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 10.10.2017.
 */

@StateStrategyType(AddToEndSingleStrategy.class) public interface IProductDetailsActivityView
    extends MvpView {
}

