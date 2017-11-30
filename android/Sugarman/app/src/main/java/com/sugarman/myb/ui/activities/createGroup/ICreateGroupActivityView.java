package com.sugarman.myb.ui.activities.createGroup;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import java.util.List;

/**
 * Created by nikita on 02.10.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface ICreateGroupActivityView
    extends MvpView {
  void fillListByCachedData(List<FacebookFriend> facebookFriends);

  void showProgress();

  void hideProgress();

  void doEventActionResponse(CustomUserEvent build);
}
