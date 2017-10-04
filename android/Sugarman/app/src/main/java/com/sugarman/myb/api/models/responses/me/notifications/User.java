package com.sugarman.myb.api.models.responses.me.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.utils.StringHelper;
import java.util.Date;

public class User implements Parcelable {

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override public User createFromParcel(Parcel in) {
      return new User(in);
    }

    @Override public User[] newArray(int size) {
      return new User[size];
    }
  };

  @SerializedName("completed_challenges_count") private int completedChallengesCount;

  @SerializedName("created_at") private String createdAt;

  @SerializedName("current_day") private String currentDay;

  @SerializedName("fbid") private String fbid;

  @SerializedName("id") private String id;

  @SerializedName("name") private String name;

  @SerializedName("picture_url") private String pictureUrl;

  @SerializedName("timezone") private String timezone;

  @SerializedName("today_progress_percent") private double todayProgressPercent;

  @SerializedName("today_steps_count") private int todayStepsCount;

  @SerializedName("updated_at") private String updatedAt;

  private Date createAtUTCDate;

  private Date updatedAtUTCDate;

  private Date currentDayUTCDate;

  User(Parcel in) {
    createdAt = in.readString();
    updatedAt = in.readString();
    currentDay = in.readString();

    completedChallengesCount = in.readInt();
    fbid = in.readString();
    id = in.readString();
    name = in.readString();
    pictureUrl = in.readString();
    timezone = in.readString();
    todayProgressPercent = in.readDouble();
    todayStepsCount = in.readInt();
  }

  public int getCompletedChallengesCount() {
    return completedChallengesCount;
  }

  public void setCompletedChallengesCount(int completedChallengesCount) {
    this.completedChallengesCount = completedChallengesCount;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getCurrentDay() {
    return currentDay;
  }

  public void setCurrentDay(String currentDay) {
    this.currentDay = currentDay;
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

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public double getTodayProgressPercent() {
    return todayProgressPercent;
  }

  public void setTodayProgressPercent(double todayProgressPercent) {
    this.todayProgressPercent = todayProgressPercent;
  }

  public int getTodayStepsCount() {
    return todayStepsCount;
  }

  public void setTodayStepsCount(int todayStepsCount) {
    this.todayStepsCount = todayStepsCount;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Date getCreateAtUTCDate() {
    if (createAtUTCDate == null) {
      createAtUTCDate = StringHelper.convertApiDate(createdAt, timezone);
    }

    return createAtUTCDate;
  }

  public Date getUpdatedAtUTCDate() {
    if (updatedAtUTCDate == null) {
      updatedAtUTCDate = StringHelper.convertApiDate(updatedAt, timezone);
    }

    return updatedAtUTCDate;
  }

  public Date getCurrentDayUTCDate() {
    if (currentDayUTCDate == null) {
      currentDayUTCDate = StringHelper.convertApiDate(currentDay, timezone);
    }

    return currentDayUTCDate;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(createdAt);
    dest.writeString(updatedAt);
    dest.writeString(currentDay);
    dest.writeInt(completedChallengesCount);
    dest.writeString(fbid);
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(pictureUrl);
    dest.writeString(timezone);
    dest.writeDouble(todayProgressPercent);
    dest.writeInt(todayStepsCount);
  }
}
