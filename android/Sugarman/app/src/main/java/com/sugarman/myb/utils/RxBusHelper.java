package com.sugarman.myb.utils;

import android.content.Intent;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

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
   @Getter private final String trackingId;

    public ShowDialogRescue(String trackingId) {
      this.trackingId = trackingId;
    }
  }

  public static class ShowDialogRescuePoke {
    @Getter private final String trackingId;

    public ShowDialogRescuePoke(String trackingId) {
      this.trackingId = trackingId;
    }
  }

  public static class ShowDialogUserSaved
  {
    @Getter private final String trackingId;

    public ShowDialogUserSaved(String trackingId) {
      this.trackingId = trackingId;
    }
  }

  public static class ShowDialogGroupSaved
  {
    @Getter private final String trackingId;

    public ShowDialogGroupSaved(String trackingId) {
      this.trackingId = trackingId;
    }
  }

  public static class EventAboutInAppPurchase {
    public final int requestCode;
    public final int resultCode;
    public final Intent data;

    public EventAboutInAppPurchase(int requestCode, int resultCode, Intent data) {
      this.requestCode=requestCode;
      this.resultCode=resultCode;
      this.data=data;
    }
  }
}
