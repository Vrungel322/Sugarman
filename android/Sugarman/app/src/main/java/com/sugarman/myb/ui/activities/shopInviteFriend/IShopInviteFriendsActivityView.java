package com.sugarman.myb.ui.activities.shopInviteFriend;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.List;

/**
 * Created by nikita on 26.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IShopInviteFriendsActivityView
    extends MvpView {
  void finishShopInviteActivity();

  void showAddFriendBtn();

  void addVkFriends(List<FacebookFriend> friends);

  void showToast();

  void hideAddFriendButton();

  void addPhoneContact(List<FacebookFriend> facebookFriends);

  void loadInviterImgUrls(List<String> imgUrls);

  void hideLoader();
}
