package com.sugarman.myb.models;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class BaseChallengeItem implements Parcelable {

  ChallengeItemType type;

  private int position;

  BaseChallengeItem(ChallengeItemType type) {
    this.type = type;
  }

  BaseChallengeItem(Parcel in) {
    position = in.readInt();
  }

  public ChallengeItemType getType() {
    return type;
  }

  protected void setType(ChallengeItemType type) {
    this.type = type;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(position);
  }
}
