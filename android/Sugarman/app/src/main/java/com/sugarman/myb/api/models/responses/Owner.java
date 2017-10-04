package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Owner implements Parcelable {

  public static final Creator<Owner> CREATOR = new Creator<Owner>() {
    @Override public Owner createFromParcel(Parcel in) {
      return new Owner(in);
    }

    @Override public Owner[] newArray(int size) {
      return new Owner[size];
    }
  };

  @SerializedName("id") private String id;

  @SerializedName("name") private String name;

  Owner(Parcel in) {
    name = in.readString();
    id = in.readString();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(id);
  }
}
