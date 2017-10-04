package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.sugarman.myb.api.models.responses.Tracking;

public class ChallengeItem extends BaseChallengeItem implements Parcelable {

  public static final Creator<ChallengeItem> CREATOR = new Creator<ChallengeItem>() {
    @Override public ChallengeItem createFromParcel(Parcel in) {
      return new ChallengeItem(in);
    }

    @Override public ChallengeItem[] newArray(int size) {
      return new ChallengeItem[size];
    }
  };

  private Tracking tracking;

  private int unreadMessages;

  public ChallengeItem() {
    super(ChallengeItemType.CHALLENGE);
  }

  private ChallengeItem(Parcel in) {
    super(in);
    tracking = in.readParcelable(Tracking.class.getClassLoader());
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(tracking, flags);
  }

  @Override public int describeContents() {
    return 0;
  }

  public Tracking getTracking() {
    return tracking;
  }

  public void setTracking(Tracking tracking) {
    this.tracking = tracking;
  }

  public void setUnreadMessages(int messages) {
    unreadMessages = messages;
  }
}
