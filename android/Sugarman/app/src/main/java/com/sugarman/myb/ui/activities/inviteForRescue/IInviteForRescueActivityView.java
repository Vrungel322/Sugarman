package com.sugarman.myb.ui.activities.inviteForRescue;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.List;

/**
 * Created by nikita on 12.12.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IInviteForRescueActivityView
    extends MvpView {
  void fillListByCachedData(List<FacebookFriend> facebookFriends);

  void showProgress();

  void hideProgress();

  //void doEventActionResponse(CustomUserEvent build);
}
