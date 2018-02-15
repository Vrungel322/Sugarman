package com.sugarman.myb.ui.fragments.list_friends_fragment;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.List;

/**
 * Created by nikita on 19.12.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IFriendListFragmentView
    extends MvpView {
  void setFriendsFb(List<FacebookFriend> allFriends);

  void onGetFriendInfoFailure(String errorMessage);

  void onGetFriendInfoSuccess(List<FacebookFriend> parsedFriends);

  void setFriendsPh(List<FacebookFriend> facebookFriends);

  void createGroupViaListener(List<FacebookFriend> toSendList);

  void editGroupViaListener(List<FacebookFriend> membersToSendByEditing);

  void inviteToShopViaListener(List<FacebookFriend> membersToSendByInviteToShop);

  void setUpUI();

  void setFriends(List<FacebookFriend> allFriendsToShow);

  void setFriendsFilter(List<FacebookFriend> facebookFriends);

  void setFriendsVk(List<FacebookFriend> allFriendsToShow);
}
