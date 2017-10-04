package com.sugarman.myb.api.models.responses.me.requests;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.StringHelper;
import java.util.Comparator;
import java.util.Date;

public class Request implements Parcelable {

  public static final Comparator<Request> BY_CREATE_AT_DESC = new Comparator<Request>() {
    @Override public int compare(Request o1, Request o2) {
      return (int) (o2.getCreatedAtUTCDate().getTime() - o1.getCreatedAtUTCDate().getTime());
    }
  };

  @SerializedName("created_at") private String createdAt;

  @SerializedName("id") private String id;

  @SerializedName("owner") private String ownerId;

  @SerializedName("tracking") private Tracking tracking;

  @SerializedName("updated_at") private String updateAt;

  @SerializedName("user") private User user;

  private Date createdAtUTCDate;

  private Date updateAtUTCDate;

  private Request(Parcel in) {
    createdAt = in.readString();
    updateAt = in.readString();

    id = in.readString();
    ownerId = in.readString();
    tracking = in.readParcelable(Tracking.class.getClassLoader());
    user = in.readParcelable(User.class.getClassLoader());
  }

  public static final Creator<Request> CREATOR = new Creator<Request>() {
    @Override public Request createFromParcel(Parcel in) {
      return new Request(in);
    }

    @Override public Request[] newArray(int size) {
      return new Request[size];
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

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
    dest.writeString(id);
    dest.writeString(ownerId);
    dest.writeParcelable(tracking, flags);
    dest.writeParcelable(user, flags);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    } else {
      Request that = (Request) o;
      return TextUtils.equals(id, that.id);
    }
  }

  @Override public int hashCode() {
    return id.hashCode();
  }
}
