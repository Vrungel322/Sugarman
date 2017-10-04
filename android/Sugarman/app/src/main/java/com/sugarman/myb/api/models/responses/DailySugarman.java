package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Y500 on 08.08.2017.
 */

public class DailySugarman implements Parcelable {

  public static final Creator<DailySugarman> CREATOR = new Creator<DailySugarman>() {
    @Override public DailySugarman createFromParcel(Parcel in) {
      return new DailySugarman(in);
    }

    @Override public DailySugarman[] newArray(int size) {
      return new DailySugarman[size];
    }
  };

  @SerializedName("last_updated") private String lastUpdated;

  @SerializedName("user") private Member user;

  protected DailySugarman(Parcel in) {
    lastUpdated = in.readString();
    user = in.readParcelable(Member.class.getClassLoader());
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(lastUpdated);
    parcel.writeParcelable(user, i);
  }

  public Member getUser() {
    return user;
  }
}
