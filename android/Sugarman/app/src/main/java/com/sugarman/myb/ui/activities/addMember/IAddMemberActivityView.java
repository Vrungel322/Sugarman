package com.sugarman.myb.ui.activities.addMember;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.List;

/**
 * Created by nikita on 26.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IAddMemberActivityView
    extends MvpView {
  void addMemberToServer(List<FacebookFriend> mFacebookFriends);

  void finishActivity();

  void fillListByCachedData(List<FacebookFriend> facebookFriends);

  void showProgress();

  void hideProgress();
}
