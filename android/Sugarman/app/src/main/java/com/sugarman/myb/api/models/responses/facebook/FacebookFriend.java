package com.sugarman.myb.api.models.responses.facebook;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import java.util.Comparator;

public class FacebookFriend extends RealmObject implements Parcelable {

  public final static int CODE_INVITABLE = 1;
  public final static int CODE_NOT_INVITABLE = 0;

  public static final Comparator<FacebookFriend> BY_NAME_ASC = new Comparator<FacebookFriend>() {
    @Override public int compare(FacebookFriend o1, FacebookFriend o2) {
      return o1.name.compareTo(o2.name);
    }
  };
  public static final Creator<FacebookFriend> CREATOR = new Creator<FacebookFriend>() {
    @Override public FacebookFriend createFromParcel(Parcel in) {
      return new FacebookFriend(in);
    }

    @Override public FacebookFriend[] newArray(int size) {
      return new FacebookFriend[size];
    }
  };
  //private String socialNetwork = new String("fb");
  public String socialNetwork;
  @Ignore String photoUrl;
  @PrimaryKey @SerializedName("id") private String id;
  @SerializedName("name") private String name;
  @Ignore @SerializedName("picture") private FacebookPicture picture;
  private int isInvitable;
  private boolean isSelected;
  private boolean isAdded;
  private boolean isPending;

  public FacebookFriend() {
  }

  public FacebookFriend(String socialNetwork, String photoUrl, String id, String name,
      int isInvitable, boolean isSelected, boolean isAdded, boolean isPending) {
    this.socialNetwork = socialNetwork;
    this.photoUrl = photoUrl;
    this.id = id;
    this.name = name;
    this.isInvitable = isInvitable;
    this.isSelected = isSelected;
    this.isAdded = isAdded;
    this.isPending = isPending;
  }

  public FacebookFriend(String id, String name, String photoUrl, int isInvitable,
      String socialNetwork) {
    this.id = id;
    this.name = name;
    this.photoUrl = photoUrl;
    this.isInvitable = isInvitable;
    this.socialNetwork = socialNetwork;
  }

  protected FacebookFriend(Parcel in) {
    photoUrl = in.readString();
    id = in.readString();
    name = in.readString();
    socialNetwork = in.readString();
    isInvitable = in.readInt();
    isSelected = in.readByte() != 0;
    isAdded = in.readByte() != 0;
    isPending = in.readByte() != 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(photoUrl);
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(socialNetwork);
    dest.writeInt(isInvitable);
    dest.writeByte((byte) (isSelected ? 1 : 0));
    dest.writeByte((byte) (isAdded ? 1 : 0));
    dest.writeByte((byte) (isPending ? 1 : 0));
  }

  @Override public int describeContents() {
    return 0;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
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

  public String getSocialNetwork() {
    return socialNetwork;
  }

  public void setSocialNetwork(String socialNetwork) {
    this.socialNetwork = socialNetwork;
  }

  public int getIsInvitable() {
    return isInvitable;
  }

  public void setIsInvitable(int isInvitable) {
    this.isInvitable = isInvitable;
  }

  public boolean isAdded() {
    return isAdded;
  }

  public void setAdded(boolean added) {
    isAdded = added;
  }

  public String getPicture() {
    if (picture != null) return picture.getData().getUrl();
    if (photoUrl != null) return photoUrl;
    return "https://sugarman-myb.s3.amazonaws.com/Group_New.png";
  }

  public void setPicture(FacebookPicture picture) {
    this.picture = picture;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public boolean isPending() {
    return isPending;
  }

  public void setPending(boolean pending) {
    isPending = pending;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    } else {
      FacebookFriend that = (FacebookFriend) o;
      return TextUtils.equals(id, that.id);
    }
  }
}
