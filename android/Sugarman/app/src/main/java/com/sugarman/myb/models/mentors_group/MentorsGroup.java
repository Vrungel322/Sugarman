package com.sugarman.myb.models.mentors_group;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.Tracking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by nikita on 01.11.2017.
 */
@AllArgsConstructor @NoArgsConstructor public class MentorsGroup extends Tracking {
  //@Getter @SerializedName("challenge_name") @Expose private String challengeName;
  //@Getter @SerializedName("created_at") @Expose private String createdAt;
  //@Getter @SerializedName("end_date") @Expose private String endDate;
  //@Getter @SerializedName("failing_members") @Expose private List<Member> failingMembers = null;
  //@Getter @SerializedName("failing_members_count") @Expose private Integer failingMembersCount;
  //@Getter @SerializedName("group") @Expose private Group group;
  //@Getter @SerializedName("group_owner_id") @Expose private String groupOwnerId;
  //@Getter @SerializedName("group_owner_name") @Expose private String groupOwnerName;
  //@Getter @SerializedName("group_steps_count") @Expose private Integer groupStepsCount;
  //@Getter @SerializedName("group_steps_count_without_me") @Expose private Integer groupStepsCountWithoutMe;
  //@Getter @SerializedName("id") @Expose private String id;
  @Getter @SerializedName("is_mentors") @Expose private Boolean isMentors;
  //@Getter @SerializedName("last_check_failing_date") @Expose private String lastCheckFailingDate;
  //@Getter @SerializedName("members") @Expose private List<Member> members = null;
  //@Getter @SerializedName("members_statuses_count") @Expose private Integer membersStatusesCount;
  //@Getter @SerializedName("new_tracking_id") @Expose private String newTrackingId;
  //@Getter @SerializedName("not_failing_members") @Expose private List<Object> notFailingMembers = null;
  //@Getter @SerializedName("pending") @Expose private List<Member> pending = null;
  //@Getter @SerializedName("sent_one_more_day_for_changes") @Expose private Boolean sentOneMoreDayForChanges;
  //@Getter @SerializedName("start_date") @Expose private String startDate;
  //@Getter @SerializedName("status") @Expose private String status;
  //@Getter @SerializedName("timezone") @Expose private String timezone;
  //@Getter @SerializedName("updated_at") @Expose private String updatedAt;
}
