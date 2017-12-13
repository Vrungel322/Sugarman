package com.sugarman.myb.utils;

import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by John on 26.01.2017.
 */

public final class RxBusHelper {

  public static class ShowAddFriendBtnEvent {
  }

  public static class HideAddFriendBtnEvent {
  }

  public static class AddMemberVkEvent {
    public List<FacebookFriend> mFacebookFriends = new ArrayList<>();

    public AddMemberVkEvent(List<FacebookFriend> facebookFriends) {
      mFacebookFriends = facebookFriends;
    }
  }

  public static class ShowDialogRescue {
   @Getter @Setter private final String trackingId;

    public ShowDialogRescue(String trackingId) {
      this.trackingId = trackingId;
    }
  }
}
