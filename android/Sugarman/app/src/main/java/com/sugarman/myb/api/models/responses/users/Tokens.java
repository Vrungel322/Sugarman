package com.sugarman.myb.api.models.responses.users;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Tokens implements Parcelable {

  @SerializedName("access_token") private String accessToken;

  @SerializedName("refresh_token") private String refreshToken;

  protected Tokens(Parcel in) {
    accessToken = in.readString();
    refreshToken = in.readString();
  }

  public static final Creator<Tokens> CREATOR = new Creator<Tokens>() {
    @Override public Tokens createFromParcel(Parcel in) {
      return new Tokens(in);
    }

    @Override public Tokens[] newArray(int size) {
      return new Tokens[size];
    }
  };

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(accessToken);
    parcel.writeString(refreshToken);
  }
}
