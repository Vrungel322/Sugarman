package com.sugarman.myb.api.models.responses.me.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.StringHelper;
import java.util.Date;

public class Notification implements Parcelable {

  public static final Creator<Notification> CREATOR = new Creator<Notification>() {
    @Override public Notification createFromParcel(Parcel in) {
      return new Notification(in);
    }

    @Override public Notification[] newArray(int size) {
      return new Notification[size];
    }
  };

  @SerializedName("created_at") private String createdAt;

  @SerializedName("id") private String id;

  @SerializedName("originator") private User originator;

  @SerializedName("recipient") private String recipient;

  @SerializedName("text") private String text;

  @SerializedName("updated_at") private String updatedAt;

  @SerializedName("user") private User user;

  @SerializedName("tracking_id") private String trackingId;

  private Date createdAtUTCDate;

  private Date updatedAtUTCDate;

  public Notification() {

  }

  protected Notification(Parcel in) {
    createdAt = in.readString();
    updatedAt = in.readString();

    id = in.readString();
    originator = in.readParcelable(User.class.getClassLoader());
    recipient = in.readString();
    text = in.readString();
    user = in.readParcelable(User.class.getClassLoader());
    trackingId = in.readString();
  }

  public String getCreatedAt() {
    return createdAt;
  }

  protected void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  protected void setId(String id) {
    this.id = id;
  }

  public User getOriginator() {
    return originator;
  }

  protected void setOriginator(User originator) {
    this.originator = originator;
  }

  public String getRecipient() {
    return recipient;
  }

  protected void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public String getText() {
    return text;
  }

  protected void setText(String text) {
    this.text = text;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  protected void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public User getUser() {
    return user;
  }

  protected void setUser(User user) {
    this.user = user;
  }

  public String getTrackingId() {
    return trackingId;
  }

  protected void setTrackingId(String trackingId) {
    this.trackingId = trackingId;
  }

  public Date getCreatedAtUTCDate() {
    if (createdAtUTCDate == null) {
      createdAtUTCDate = StringHelper.convertApiDate(createdAt, Constants.DEF_TIMEZONE);
    }

    return createdAtUTCDate;
  }

  public Date getUpdatedAtUTCDate() {
    if (updatedAtUTCDate == null) {
      updatedAtUTCDate = StringHelper.convertApiDate(updatedAt, Constants.DEF_TIMEZONE);
    }

    return updatedAtUTCDate;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(createdAt);
    dest.writeString(updatedAt);
    dest.writeString(id);
    dest.writeParcelable(originator, flags);
    dest.writeString(recipient);
    dest.writeString(text);
    dest.writeParcelable(user, flags);
    dest.writeString(trackingId);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null || getClass() != o.getClass()) {
      return false;
    } else {
      Notification that = (Notification) o;
      return TextUtils.equals(id, that.id);
    }
  }

  @Override public int hashCode() {
    return id.hashCode();
  }
}
