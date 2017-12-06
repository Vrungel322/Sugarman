package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.sugarman.myb.api.models.responses.Tracking;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 01.11.2017.
 */
public class ChallengeMentorItem extends BaseChallengeItem implements Parcelable {
  public static final Creator<ChallengeMentorItem> CREATOR = new Creator<ChallengeMentorItem>() {
    @Override public ChallengeMentorItem createFromParcel(Parcel in) {
      return new ChallengeMentorItem(in);
    }

    @Override public ChallengeMentorItem[] newArray(int size) {
      return new ChallengeMentorItem[size];
    }
  };
  @Getter @Setter public Tracking tracking;

  public ChallengeMentorItem() {
    super(ChallengeItemType.MENTORS_CHALLENGE);
  }

  protected ChallengeMentorItem(Parcel in) {
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
