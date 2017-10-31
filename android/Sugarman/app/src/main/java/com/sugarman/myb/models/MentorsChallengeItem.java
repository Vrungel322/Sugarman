package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yegoryeriomin on 10/31/17.
 */

public class MentorsChallengeItem extends BaseChallengeItem implements Parcelable {
  public MentorsChallengeItem() {
    super(ChallengeItemType.MENTORS_CHALLENGE);
  }

  protected MentorsChallengeItem(Parcel in) {
    super(in);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<MentorsChallengeItem> CREATOR = new Creator<MentorsChallengeItem>() {
    @Override public MentorsChallengeItem createFromParcel(Parcel in) {
      return new MentorsChallengeItem(in);
    }

    @Override public MentorsChallengeItem[] newArray(int size) {
      return new MentorsChallengeItem[size];
    }
  };
}
