package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Group implements Parcelable {

  public static final Creator<Group> CREATOR = new Creator<Group>() {
    @Override public Group createFromParcel(Parcel in) {
      return new Group(in);
    }

    @Override public Group[] newArray(int size) {
      return new Group[size];
    }
  };

  @SerializedName("id") private String id;

  @SerializedName("name") private String name;

  @SerializedName("owner") private Owner owner;

  @SerializedName("picture_url") private String pictureUrl;

  Group(Parcel in) {
    id = in.readString();
    name = in.readString();
    owner = in.readParcelable(Owner.class.getClassLoader());
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

  public Owner getOwner() {
    return owner;
  }

  public void setOwner(Owner owner) {
    this.owner = owner;
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
    dest.writeParcelable(owner, flags);
    dest.writeString(pictureUrl);
  }
}
