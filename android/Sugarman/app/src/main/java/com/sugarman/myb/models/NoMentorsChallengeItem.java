package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yegoryeriomin on 10/27/17.
 */

public class NoMentorsChallengeItem extends BaseChallengeItem implements Parcelable {
  public NoMentorsChallengeItem() {
    super(ChallengeItemType.NO_MENTORS_CHALLENGE);
  }

  protected NoMentorsChallengeItem(Parcel in) {
    super(in);
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<NoMentorsChallengeItem> CREATOR =
      new Creator<NoMentorsChallengeItem>() {
        @Override public NoMentorsChallengeItem createFromParcel(Parcel in) {
          return new NoMentorsChallengeItem(in);
        }

        @Override public NoMentorsChallengeItem[] newArray(int size) {
          return new NoMentorsChallengeItem[size];
        }
      };
}
