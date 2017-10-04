package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NoChallengeItem extends BaseChallengeItem implements Parcelable {

  public static final Creator<NoChallengeItem> CREATOR = new Creator<NoChallengeItem>() {
    @Override public NoChallengeItem createFromParcel(Parcel in) {
      return new NoChallengeItem(in);
    }

    @Override public NoChallengeItem[] newArray(int size) {
      return new NoChallengeItem[size];
    }
  };

  public NoChallengeItem() {
    super(ChallengeItemType.NO_CHALLENGES);
  }

  private NoChallengeItem(Parcel in) {
    super(in);
  }

  @Override public int describeContents() {
    return 0;
  }
}
