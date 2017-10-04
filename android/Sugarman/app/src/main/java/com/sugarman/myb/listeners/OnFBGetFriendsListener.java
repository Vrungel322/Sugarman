package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.List;

public interface OnFBGetFriendsListener {

  void onGetFacebookFriendsSuccess(List<FacebookFriend> friends,
      List<FacebookFriend> invitableFriends);

  void onGetFacebookFriendsFailure(String message);

  void onGetFriendInfoSuccess(List<FacebookFriend> convertedFriends);

  void onGetFriendInfoFailure(String message);
}
