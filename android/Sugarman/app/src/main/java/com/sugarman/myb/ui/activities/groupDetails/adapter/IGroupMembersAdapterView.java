package com.sugarman.myb.ui.activities.groupDetails.adapter;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 06.11.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IGroupMembersAdapterView
    extends MvpView {
  void showTest(String s);
}
