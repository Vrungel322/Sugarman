package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.Comparator;

public class Member implements Parcelable {

  public static final int ACTION_ZERO = 0;
  public static final int ACTION_LAZY = 1;
  public static final int ACTION_WALK = 2;
  public static final int ACTION_RUN = 3;

  public static final Comparator<Member> BY_STEPS_ASC = new Comparator<Member>() {
    @Override public int compare(Member o1, Member o2) {
      return o1.steps - o2.steps;
    }
  };
  public static final Comparator<Member> BY_STEPS_DESC = new Comparator<Member>() {
    @Override public int compare(Member o1, Member o2) {
      return o2.steps - o1.steps;
    }
  };

  @SerializedName("steps") public int steps;
  @SerializedName("action") private int action;
  @SerializedName("ass_kick_count") private int kickCount;
  @SerializedName("fbid") private String fbid;
  @SerializedName("id") private String id;
  @SerializedName("name") private String name;
  @SerializedName("picture_url") private String pictureUrl;
  @SerializedName("status") private String status;
  @SerializedName("phone_number") private String phoneNumber;
  @SerializedName("vkid") private String vkId;

  public Member() {

  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    this.steps = steps;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public int getKickCount() {
    return kickCount;
  }

  public void setKickCount(int kickCount) {
    this.kickCount = kickCount;
  }

  public String getFbid() {
    return fbid;
  }

  public void setFbid(String fbid) {
    this.fbid = fbid;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getVkId() {
    return vkId;
  }

  public void setVkId(String vkId) {
    this.vkId = vkId;
  }

  protected Member(Parcel in) {
    steps = in.readInt();
    action = in.readInt();
    kickCount = in.readInt();
    fbid = in.readString();
    id = in.readString();
    name = in.readString();
    pictureUrl = in.readString();
    status = in.readString();
    phoneNumber = in.readString();
    vkId = in.readString();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(steps);
    dest.writeInt(action);
    dest.writeInt(kickCount);
    dest.writeString(fbid);
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(pictureUrl);
    dest.writeString(status);
    dest.writeString(phoneNumber);
    dest.writeString(vkId);
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<Member> CREATOR = new Creator<Member>() {
    @Override public Member createFromParcel(Parcel in) {
      return new Member(in);
    }

    @Override public Member[] newArray(int size) {
      return new Member[size];
    }
  };
}
