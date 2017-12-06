package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.sugarman.myb.api.models.responses.Tracking;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 06.12.2017.
 */

public class ChallengeRescueItem extends BaseChallengeItem implements Parcelable {
  public static final Creator<ChallengeRescueItem> CREATOR = new Creator<ChallengeRescueItem>() {
    @Override public ChallengeRescueItem createFromParcel(Parcel in) {
      return new ChallengeRescueItem(in);
    }

    @Override public ChallengeRescueItem[] newArray(int size) {
      return new ChallengeRescueItem[size];
    }
  };
  @Getter @Setter private Tracking tracking;

  public ChallengeRescueItem() {
    super(ChallengeItemType.RESCUE_GROUP);
  }

  protected ChallengeRescueItem(Parcel in) {
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
}
