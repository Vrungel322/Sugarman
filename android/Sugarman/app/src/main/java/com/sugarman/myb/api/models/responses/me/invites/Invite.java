package com.sugarman.myb.api.models.responses.me.invites;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.StringHelper;
import java.util.Comparator;
import java.util.Date;

public class Invite implements Parcelable {

  public static final Comparator<Invite> BY_CREATE_AT_DESC = new Comparator<Invite>() {
    @Override public int compare(Invite o1, Invite o2) {
      return (int) (o2.getCreatedAtUTCDate().getTime() - o1.getCreatedAtUTCDate().getTime());
    }
  };

  @SerializedName("created_at") private String createdAt;

  @SerializedName("did_send_expiry_push") private final boolean didSendExpiryPush;

  @SerializedName("id") private String id;

  @SerializedName("inviter") private final Inviter inviter;

  @SerializedName("tracking") private Tracking tracking;

  @SerializedName("updated_at") private String updateAt;

  private Date createdAtUTCDate;

  private Date updateAtUTCDate;

  private Invite(Parcel in) {
    createdAt = in.readString();
    updateAt = in.readString();

    didSendExpiryPush = in.readByte() != 0;
    id = in.readString();
    inviter = in.readParcelable(Inviter.class.getClassLoader());
    tracking = in.readParcelable(Tracking.class.getClassLoader());
  }

  public static final Creator<Invite> CREATOR = new Creator<Invite>() {
    @Override public Invite createFromParcel(Parcel in) {
      return new Invite(in);
    }

    @Override public Invite[] newArray(int size) {
      return new Invite[size];
    }
  };

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Tracking getTracking() {
    return tracking;
  }

  public void setTracking(Tracking tracking) {
    this.tracking = tracking;
  }

  public String getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(String updateAt) {
    this.updateAt = updateAt;
  }

  public Date getCreatedAtUTCDate() {
    if (createdAtUTCDate == null) {
      createdAtUTCDate = StringHelper.convertApiDate(createdAt, Constants.DEF_TIMEZONE);
    }

    return createdAtUTCDate;
  }

  private Date getUpdateAtUTCDate() {
    if (updateAtUTCDate == null) {
      updateAtUTCDate = StringHelper.convertApiDate(updateAt, Constants.DEF_TIMEZONE);
    }

    return updateAtUTCDate;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(createdAt);
    dest.writeString(updateAt);
    dest.writeByte((byte) (didSendExpiryPush ? 1 : 0));
    dest.writeString(id);
    dest.writeParcelable(inviter, flags);
    dest.writeParcelable(tracking, flags);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    } else {
      Invite that = (Invite) o;
      return TextUtils.equals(id, that.id);
    }
  }

  @Override public int hashCode() {
    return id.hashCode();
  }
}
