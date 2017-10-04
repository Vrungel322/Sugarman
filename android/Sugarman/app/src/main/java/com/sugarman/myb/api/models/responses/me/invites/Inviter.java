package com.sugarman.myb.api.models.responses.me.invites;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Inviter implements Parcelable {

  public static final Creator<Inviter> CREATOR = new Creator<Inviter>() {
    @Override public Inviter createFromParcel(Parcel in) {
      return new Inviter(in);
    }

    @Override public Inviter[] newArray(int size) {
      return new Inviter[size];
    }
  };

  @SerializedName("id") private String id;

  @SerializedName("name") private String name;

  @SerializedName("picture_url") private String pictureUrl;

  Inviter(Parcel in) {
    id = in.readString();
    name = in.readString();
    pictureUrl = in.readString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(pictureUrl);
  }
}
