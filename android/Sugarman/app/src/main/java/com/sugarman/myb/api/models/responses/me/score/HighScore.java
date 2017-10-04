package com.sugarman.myb.api.models.responses.me.score;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.utils.StringHelper;
import java.util.Date;

public class HighScore {

  @SerializedName("challenge_name") private String challengeName;

  @SerializedName("created_at") private String createdAt;

  @SerializedName("end_date") private String endDate;

  @SerializedName("failing_members") private Member[] failinigMembers;

  @SerializedName("group") private Group group;

  @SerializedName("group_owner_id") private String groupOwnerId;

  @SerializedName("group_owner_name") private String groupOwnerName;

  @SerializedName("group_steps_count") private int groupStepsCount;

  @SerializedName("id") private String id;

  @SerializedName("members") private Member[] members;

  @SerializedName("my_ass_kick_count") private int myAssKickCount;

  @SerializedName("my_position") private int myPosition;

  @SerializedName("pending") private Member[] pending;

  @SerializedName("sent_one_more_day_for_changes") private boolean isSentOneMoreDayForChanges;

  @SerializedName("start_date") private String startDate;

  @SerializedName("status") private String status;

  @SerializedName("timezone") private String timezone;

  @SerializedName("updated_at") private String updateAt;

  private Date createdAtUTCDate;

  private Date endAtUTCDate;

  private Date startUTCDate;

  private Date updateAtUTCDate;

  public String getChallengeName() {
    return challengeName;
  }

  public void setChallengeName(String challengeName) {
    this.challengeName = challengeName;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public Member[] getFailinigMembers() {
    return failinigMembers;
  }

  public void setFailinigMembers(Member[] failinigMembers) {
    this.failinigMembers = failinigMembers;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public String getGroupOwnerId() {
    return groupOwnerId;
  }

  public void setGroupOwnerId(String groupOwnerId) {
    this.groupOwnerId = groupOwnerId;
  }

  public String getGroupOwnerName() {
    return groupOwnerName;
  }

  public void setGroupOwnerName(String groupOwnerName) {
    this.groupOwnerName = groupOwnerName;
  }

  public int getGroupStepsCount() {
    return groupStepsCount;
  }

  public void setGroupStepsCount(int groupStepsCount) {
    this.groupStepsCount = groupStepsCount;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Member[] getMembers() {
    return members;
  }

  public void setMembers(Member[] members) {
    this.members = members;
  }

  public Member[] getPending() {
    return pending;
  }

  public void setPending(Member[] pending) {
    this.pending = pending;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(String updateAt) {
    this.updateAt = updateAt;
  }

  public int getMyAssKickCount() {
    return myAssKickCount;
  }

  public void setMyAssKickCount(int myAssKickCount) {
    this.myAssKickCount = myAssKickCount;
  }

  public int getMyPosition() {
    return myPosition;
  }

  public void setMyPosition(int myPosition) {
    this.myPosition = myPosition;
  }

  public boolean isSentOneMoreDayForChanges() {
    return isSentOneMoreDayForChanges;
  }

  public void setSentOneMoreDayForChanges(boolean sentOneMoreDayForChanges) {
    isSentOneMoreDayForChanges = sentOneMoreDayForChanges;
  }

  public Date getCreatedAtUTCDate() {
    if (createdAtUTCDate == null) {
      createdAtUTCDate = StringHelper.convertApiDate(createdAt, timezone);
    }

    return createdAtUTCDate;
  }

  public Date getEndAtUTCDate() {
    if (endAtUTCDate == null) {
      endAtUTCDate = StringHelper.convertApiDate(endDate, timezone);
    }

    return endAtUTCDate;
  }

  public Date getStartUTCDate() {
    if (startUTCDate == null) {
      startUTCDate = StringHelper.convertApiDate(startDate, timezone);
    }

    return startUTCDate;
  }

  public Date getUpdateAtUTCDate() {
    if (updateAtUTCDate == null) {
      updateAtUTCDate = StringHelper.convertApiDate(updateAt, timezone);
    }

    return updateAtUTCDate;
  }
}
