package com.sugarman.myb.api.models.responses.me.groups;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.StringHelper;
import java.util.Date;

public class CreatedGroup {

  @SerializedName("completed_challenges_count") private int completedChallengesCount;

  @SerializedName("created_at") private String createdAt;

  @SerializedName("id") private String id;

  @SerializedName("members_string") private String membersString;

  @SerializedName("name") private String name;

  @SerializedName("picture_url") private String pictureUrl;

  @SerializedName("updated_at") private String updatedAt;

  private Date updateAtUTCDate;

  private Date createdAtUTCDate;

  public int getCompletedChallengesCount() {
    return completedChallengesCount;
  }

  public void setCompletedChallengesCount(int completedChallengesCount) {
    this.completedChallengesCount = completedChallengesCount;
  }

  public Date getCreatedAt() {
    if (createdAtUTCDate == null) {
      createdAtUTCDate =
          StringHelper.convertApiDate(createdAt, SharedPreferenceHelper.getTimezone());
    }
    return createdAtUTCDate;
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

  public String getMembersString() {
    return membersString;
  }

  public void setMembersString(String membersString) {
    this.membersString = membersString;
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

  public Date getUpdatedAt() {
    if (updateAtUTCDate == null) {
      updateAtUTCDate =
          StringHelper.convertApiDate(updatedAt, SharedPreferenceHelper.getTimezone());
    }
    return updateAtUTCDate;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
