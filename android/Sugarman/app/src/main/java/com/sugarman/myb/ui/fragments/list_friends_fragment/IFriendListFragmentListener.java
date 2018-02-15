package com.sugarman.myb.ui.fragments.list_friends_fragment;

import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.List;

/**
 * Created by nikita on 19.12.2017.
 */

public interface IFriendListFragmentListener {
  void createGroup(List<FacebookFriend> friendList, String groupName);

  void editGroup(List<FacebookFriend> membersToSendByEditing, String groupName);

  void inviteFriendToShop(List<FacebookFriend> friendList);
}
