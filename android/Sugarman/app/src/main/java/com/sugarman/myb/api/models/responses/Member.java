package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.Comparator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Member implements Parcelable{

  public static final int ACTION_ZERO = 0;
  public static final int ACTION_LAZY = 1;
  public static final int ACTION_WALK = 2;
  public static final int ACTION_RUN = 3;
  public static final int FAIL_STATUS_FAILUER = 1;
  public static final int FAIL_STATUS_NORMAL = 0;
  public static final int FAIL_STATUS_SAVED = 2;

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

 @Getter @Setter @SerializedName("steps") public int steps;
 @Getter @Setter @SerializedName("action") private int action;
 @Getter @Setter @SerializedName("ass_kick_count") private int kickCount;
 @Getter @Setter @SerializedName("fbid") private String fbid;
 @Getter @Setter @SerializedName("id") private String id;
 @Getter @Setter @SerializedName("name") private String name;
 @Getter @Setter @SerializedName("picture_url") private String pictureUrl;
 @Getter @Setter @SerializedName("status") private String status;
 @Getter @Setter @SerializedName("phone_number") private String phoneNumber;
 @Getter @Setter @SerializedName("vkid") private String vkId;
 @Getter @Setter @SerializedName("failed_status") private int failureStatus;

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
    failureStatus = in.readInt();
  }

  public static final Creator<Member> CREATOR = new Creator<Member>() {
    @Override public Member createFromParcel(Parcel in) {
      return new Member(in);
    }

    @Override public Member[] newArray(int size) {
      return new Member[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(steps);
    parcel.writeInt(action);
    parcel.writeInt(kickCount);
    parcel.writeString(fbid);
    parcel.writeString(id);
    parcel.writeString(name);
    parcel.writeString(pictureUrl);
    parcel.writeString(status);
    parcel.writeString(phoneNumber);
    parcel.writeString(vkId);
    parcel.writeInt(failureStatus);
  }
}
