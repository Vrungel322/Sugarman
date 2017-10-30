package com.sugarman.myb.models.mentor;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nikita on 27.10.2017.
 */

public class MemberOfMentorsGroup  implements Parcelable{
  @SerializedName("name")private String name;
  @SerializedName("img_url") private String imgUrl;
  @SerializedName("id_member") private String idMember;

  public MemberOfMentorsGroup(String name, String imgUrl, String idMember) {
    this.name = name;
    this.imgUrl = imgUrl;
    this.idMember = idMember;
  }

  protected MemberOfMentorsGroup(Parcel in) {
    name = in.readString();
    imgUrl = in.readString();
    idMember = in.readString();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(imgUrl);
    dest.writeString(idMember);
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<MemberOfMentorsGroup> CREATOR = new Creator<MemberOfMentorsGroup>() {
    @Override public MemberOfMentorsGroup createFromParcel(Parcel in) {
      return new MemberOfMentorsGroup(in);
    }

    @Override public MemberOfMentorsGroup[] newArray(int size) {
      return new MemberOfMentorsGroup[size];
    }
  };

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getIdMember() {
    return idMember;
  }

  public void setIdMember(String idMember) {
    this.idMember = idMember;
  }
}
