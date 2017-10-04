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

  public static final Creator<Member> CREATOR = new Creator<Member>() {
    @Override public Member createFromParcel(Parcel in) {
      return new Member(in);
    }

    @Override public Member[] newArray(int size) {
      return new Member[size];
    }
  };

  @SerializedName("action") private int action;

  @SerializedName("ass_kick_count") private int kickCount;

  @SerializedName("fbid") private String fbid;

  @SerializedName("id") private String id;

  @SerializedName("name") private String name;

  @SerializedName("picture_url") private String pictureUrl;

  @SerializedName("steps") public int steps;

  @SerializedName("status") private String status;

  public Member() {

  }

  private Member(Parcel in) {
    action = in.readInt();
    kickCount = in.readInt();
    fbid = in.readString();
    //vkid = in.readString();
    //phoneNumber = in.readString();
    id = in.readString();
    name = in.readString();
    pictureUrl = in.readString();
    steps = in.readInt();
    status = in.readString();
  }

  public int getAction() {
    return action;
  }

  protected void setAction(int action) {
    this.action = action;
  }

  public String getFbid() {
    return fbid;
  }

  protected void setFbid(String fbid) {
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

  protected void setName(String name) {
    this.name = name;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  protected void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    this.steps = steps;
  }

  public String getStatus() {
    return status;
  }

  protected void setStatus(String status) {
    this.status = status;
  }

  public int getKickCount() {
    return kickCount;
  }

  protected void setKickCount(int kickCount) {
    this.kickCount = kickCount;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(action);
    dest.writeInt(kickCount);
    dest.writeString(fbid);
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(pictureUrl);
    dest.writeInt(steps);
    dest.writeString(status);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Member member = (Member) o;

    return this.id.equals(member.getId());
  }

  @Override public int hashCode() {
    return this.id.hashCode();
  }
}
