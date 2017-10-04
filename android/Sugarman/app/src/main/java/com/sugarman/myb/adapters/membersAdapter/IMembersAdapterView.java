package com.sugarman.myb.adapters.membersAdapter;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 26.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IMembersAdapterView
    extends MvpView {
}
