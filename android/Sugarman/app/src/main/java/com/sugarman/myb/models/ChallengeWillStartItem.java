package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.sugarman.myb.api.models.responses.Tracking;

public class ChallengeWillStartItem extends BaseChallengeItem implements Parcelable {

  public static final Creator<ChallengeWillStartItem> CREATOR =
      new Creator<ChallengeWillStartItem>() {
        @Override public ChallengeWillStartItem createFromParcel(Parcel in) {
          return new ChallengeWillStartItem(in);
        }

        @Override public ChallengeWillStartItem[] newArray(int size) {
          return new ChallengeWillStartItem[size];
        }
      };

  private Tracking tracking;

  public ChallengeWillStartItem() {
    super(ChallengeItemType.CHALLENGE_WILL_START);
  }

  private ChallengeWillStartItem(Parcel in) {
    super(in);
    tracking = in.readParcelable(Tracking.class.getClassLoader());
  }

  public Tracking getTracking() {
    return tracking;
  }

  public void setTracking(Tracking tracking) {
    this.tracking = tracking;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(tracking, flags);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    } else {
      ChallengeWillStartItem that = (ChallengeWillStartItem) o;
      return TextUtils.equals(tracking == null ? null : tracking.getId(),
          that.tracking == null ? null : that.tracking.getId());
    }
  }

  @Override public int hashCode() {
    return tracking == null ? type.hashCode() : tracking.getId().hashCode();
  }
}
