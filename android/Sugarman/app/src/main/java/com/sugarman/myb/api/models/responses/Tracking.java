package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.utils.StringHelper;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;

public class Tracking implements Parcelable {

  public static final Comparator<Tracking> BY_END_DATE_ASC_AND_MEMBER_COUNT_DESC =
      new Comparator<Tracking>() {
        @Override public int compare(Tracking o1, Tracking o2) {
          int dif = (int) (o1.getEndUTCDate().getTime() - o2.getEndUTCDate().getTime());
          return dif == 0 ? o2.members.length - o1.members.length : dif;
        }
      };


  @SerializedName("challenge_name") private String challengeName;

  @SerializedName("created_at") private String createdAt;

  @SerializedName("daily_winner") private DailySugarman dailySugarman;

  @SerializedName("end_date") private String endDate;

  @SerializedName("failing_members") private Member[] failingMembers;

  @SerializedName("group") private Group group;

  @SerializedName("group_owner_id") private String groupOwnerId;

  @SerializedName("group_owner_name") private String groupOnwerName;

  @SerializedName("group_steps_count") private int groupStepsCount;

  @SerializedName("group_steps_count_without_me") private int groupStepsCountWithoutMe;

  @SerializedName("id") private String id;

  @SerializedName("members") private Member[] members;

  @Getter @Setter @SerializedName("is_mentors") boolean isMentors;

  protected Tracking(Parcel in) {
    challengeName = in.readString();
    createdAt = in.readString();
    dailySugarman = in.readParcelable(DailySugarman.class.getClassLoader());
    endDate = in.readString();
    failingMembers = in.createTypedArray(Member.CREATOR);
    group = in.readParcelable(Group.class.getClassLoader());
    groupOwnerId = in.readString();
    groupOnwerName = in.readString();
    groupStepsCount = in.readInt();
    groupStepsCountWithoutMe = in.readInt();
    id = in.readString();
    members = in.createTypedArray(Member.CREATOR);
    isMentors = in.readByte() != 0;
    notFailingMembers = in.createTypedArray(Member.CREATOR);
    pending = in.createTypedArray(Member.CREATOR);
    startDate = in.readString();
    status = in.readString();
    timezone = in.readString();
    updatedAt = in.readString();
  }

  public static final Creator<Tracking> CREATOR = new Creator<Tracking>() {
    @Override public Tracking createFromParcel(Parcel in) {
      return new Tracking(in);
    }

    @Override public Tracking[] newArray(int size) {
      return new Tracking[size];
    }
  };

  public Member[] getNotFailingMembers() {
    return notFailingMembers;
  }

  public void setNotFailingMembers(Member[] notFailingMembers) {
    this.notFailingMembers = notFailingMembers;
  }

  @SerializedName("not_failing_members") private Member[] notFailingMembers;

  @SerializedName("pending") private Member[] pending;

  @SerializedName("start_date") private String startDate;

  @SerializedName("status") private String status;

  @SerializedName("timezone") private String timezone;

  @SerializedName("updated_at") private String updatedAt;

  private Date createdAtUTCDate;

  private Date endUTCDate;

  private Date startUTCDate;

  private Date updateUTCDate;

  public Tracking() {

  }


  public String getChallengeName() {
    return challengeName;
  }

  protected void setChallengeName(String challengeName) {
    this.challengeName = challengeName;
  }

  public Member[] getFailingMembers() {
    return failingMembers;
  }

  protected void setFailingMembers(Member[] failingMembers) {
    this.failingMembers = failingMembers;
  }

  public Group getGroup() {
    return group;
  }

  protected void setGroup(Group group) {
    this.group = group;
  }

  public String getGroupOwnerId() {
    return groupOwnerId;
  }

  protected void setGroupOwnerId(String groupOwnerId) {
    this.groupOwnerId = groupOwnerId;
  }

  public String getGroupOnwerName() {
    return groupOnwerName;
  }

  protected void setGroupOnwerName(String groupOnwerName) {
    this.groupOnwerName = groupOnwerName;
  }

  public int getGroupStepsCount() {
    return groupStepsCount;
  }

  protected void setGroupStepsCount(int groupStepsCount) {
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

  protected void setMembers(Member[] members) {
    this.members = members;
  }

  public Member[] getPending() {
    return pending;
  }

  protected void setPending(Member[] pending) {
    this.pending = pending;
  }

  public Date getCreatedAtUTCDate() {
    if (createdAtUTCDate == null) {
      createdAtUTCDate = StringHelper.convertApiDate(createdAt, timezone);
    }
    return createdAtUTCDate;
  }

  protected void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  private Date getEndUTCDate() {
    if (endUTCDate == null) {
      endUTCDate = StringHelper.convertApiDate(endDate, timezone);
    }

    return endUTCDate;
  }

  protected void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public Date getStartUTCDate() {
    if (startUTCDate == null) {
      startUTCDate = StringHelper.convertApiDate(startDate,
          TimeZone.getDefault().toString()); // workaround problems
    }
    return startUTCDate;
  }

  protected void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public Date getUpdateUTCDate() {
    if (updateUTCDate == null) {
      updateUTCDate = StringHelper.convertApiDate(updatedAt, timezone);
    }
    return updateUTCDate;
  }

  protected void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getStatus() {
    return status;
  }

  protected void setStatus(String status) {
    this.status = status;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public boolean hasDailyWinner() {
    if (dailySugarman == null) return false;
    return true;
  }

  public DailySugarman getDailySugarman() {
    return dailySugarman;
  }


  public int getGroupStepsCountWithoutMe() {
    return groupStepsCountWithoutMe;
  }

  public void setGroupStepsCountWithoutMe(int groupStepsCountWithoutMe) {
    this.groupStepsCountWithoutMe = groupStepsCountWithoutMe;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(challengeName);
    parcel.writeString(createdAt);
    parcel.writeParcelable(dailySugarman, i);
    parcel.writeString(endDate);
    parcel.writeTypedArray(failingMembers, i);
    parcel.writeParcelable(group, i);
    parcel.writeString(groupOwnerId);
    parcel.writeString(groupOnwerName);
    parcel.writeInt(groupStepsCount);
    parcel.writeInt(groupStepsCountWithoutMe);
    parcel.writeString(id);
    parcel.writeTypedArray(members, i);
    parcel.writeByte((byte) (isMentors ? 1 : 0));
    parcel.writeTypedArray(notFailingMembers, i);
    parcel.writeTypedArray(pending, i);
    parcel.writeString(startDate);
    parcel.writeString(status);
    parcel.writeString(timezone);
    parcel.writeString(updatedAt);
  }
}
