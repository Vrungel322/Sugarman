package com.sugarman.myb.api.models.responses.users;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.models.iab.SubscriptionEntity;
import com.sugarman.myb.utils.StringHelper;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class User {

  @SerializedName("active_trackings_created") private int activeTrackingsCreated;

  @SerializedName("completed_challenges_count") private int completedChallengesCount;

  @SerializedName("created_at") private String createdAt;

  @Getter @Setter @SerializedName("is_mentor") private Boolean isMentor;

  @SerializedName("current_day") private String currentDay;

  @SerializedName("completed_days_count") private int completedDaysCount;

  @SerializedName("email") private String email;

  @SerializedName("fbid") private String facebookId;

  @SerializedName("vkid") private String vkid;

  @SerializedName("phone_number") private String phoneNumber;

  @SerializedName("id") private String id;

  @SerializedName("name") private String name;

  @SerializedName("picture_url") private String pictureUrl;

  @SerializedName("timezone") private String timezone;

  @SerializedName("today_steps_count") private int todayStepsCount;

  @SerializedName("today_progress_percent") private double todayProgressPercent;

  @SerializedName("updated_at") private String updatedAt;

  @SerializedName("phone_OTP") private String phoneOTP;

  @SerializedName("is_pending") private String isPending;

  @SerializedName("level") private int level;

  @SerializedName("need_to_do_OTP") private Boolean needOTP;

  @SerializedName("groups_limit") @Getter @Setter private String groupsLimit;

  @SerializedName("ab_testing") @Getter @Setter private Integer aOrB;

  @SerializedName("remote_logging_enabled") @Getter @Setter private boolean remoteLoggingEnabled;

  @SerializedName("subscriptions") @Getter @Setter private List<SubscriptionEntity>
      subscriptionEntities;

  private Date createUTCDate;

  private Date currentDayUTCDate;

  private Date updatedAtUTCDate;

  public String getPhoneOTP() {
    return phoneOTP;
  }

  public String getIsPending() {
    return isPending;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getVkid() {
    return vkid;
  }

  public String getEmail() {
    return email;
  }

  public int getLevel() {
    return level;
  }

  public Date getCreateUTCDate() {
    if (createUTCDate == null) {
      createUTCDate = StringHelper.convertApiDate(createdAt, timezone);
    }

    return createUTCDate;
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

  public Boolean getNeedOTP() {
    return needOTP;
  }

  public void setNeedOTP(Boolean needOTP) {
    this.needOTP = needOTP;
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

  public String getFacebookId() {
    return facebookId;
  }

  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
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

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public int getTodayStepsCount() {
    return todayStepsCount;
  }

  public void setTodayStepsCount(int todayStepsCount) {
    this.todayStepsCount = todayStepsCount;
  }

  public double getTodayProgressPercent() {
    return todayProgressPercent;
  }

  public void setTodayProgressPercent(double todayProgressPercent) {
    this.todayProgressPercent = todayProgressPercent;
  }

  public String getCurrentDay() {
    return currentDay;
  }

  public void setCurrentDay(String currentDay) {
    this.currentDay = currentDay;
  }

  public int getActiveTrackingsCreated() {
    return activeTrackingsCreated;
  }

  public void setActiveTrackingsCreated(int activeTrackingsCreated) {
    this.activeTrackingsCreated = activeTrackingsCreated;
  }

  public int getCompletedDaysCount() {
    return completedDaysCount;
  }

  public void setCompletedDaysCount(int completedDaysCount) {
    this.completedDaysCount = completedDaysCount;
  }

  @Override public String toString() {
    return "User{"
        + "activeTrackingsCreated="
        + activeTrackingsCreated
        + ", completedChallengesCount="
        + completedChallengesCount
        + ", createdAt='"
        + createdAt
        + '\''
        + ", currentDay='"
        + currentDay
        + '\''
        + ", completedDaysCount="
        + completedDaysCount
        + ", facebookId='"
        + facebookId
        + '\''
        + ", id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", pictureUrl='"
        + pictureUrl
        + '\''
        + ", timezone='"
        + timezone
        + '\''
        + ", todayStepsCount="
        + todayStepsCount
        + ", todayProgressPercent="
        + todayProgressPercent
        + ", updatedAt='"
        + updatedAt
        + '\''
        + ", createUTCDate="
        + createUTCDate
        + ", currentDayUTCDate="
        + currentDayUTCDate
        + ", updatedAtUTCDate="
        + updatedAtUTCDate
        + ", phoneOTP="
        + phoneOTP
        + ", isPending="
        + isPending
        + '}';
  }
}
