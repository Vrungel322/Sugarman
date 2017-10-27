package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by nikita on 27.10.2017.
 */

public class MentorEntity {
  @SerializedName("id")private String mentorId;
  @SerializedName("picture_url") private String mentorImgUrl;
  @SerializedName("name")private String mentorName;
  @SerializedName("rating")private String mentorRating;
  @SerializedName("mentor_num_comments") private String mentorNumComments;
  @SerializedName("description")private String mentorDescription;
  @SerializedName("skills")private List<MentorsSkills> mentorSkills;
  @SerializedName("members_statuses")private List<MemberOfMentorsGroup> membersOfMentorsGroup;

  public MentorEntity(String mentorId, String mentorImgUrl, String mentorName, String mentorRating,
      String mentorNumComments, String mentorDescription, List<MentorsSkills> mentorSkills,
      List<MemberOfMentorsGroup> membersOfMentorsGroup) {
    this.mentorId = mentorId;
    this.mentorImgUrl = mentorImgUrl;
    this.mentorName = mentorName;
    this.mentorRating = mentorRating;
    this.mentorNumComments = mentorNumComments;
    this.mentorDescription = mentorDescription;
    this.mentorSkills = mentorSkills;
    this.membersOfMentorsGroup = membersOfMentorsGroup;
  }

  public String getMentorId() {
    return mentorId;
  }

  public void setMentorId(String mentorId) {
    this.mentorId = mentorId;
  }

  public String getMentorImgUrl() {
    return mentorImgUrl;
  }

  public void setMentorImgUrl(String mentorImgUrl) {
    this.mentorImgUrl = mentorImgUrl;
  }

  public String getMentorName() {
    return mentorName;
  }

  public void setMentorName(String mentorName) {
    this.mentorName = mentorName;
  }

  public String getMentorRating() {
    return mentorRating;
  }

  public void setMentorRating(String mentorRating) {
    this.mentorRating = mentorRating;
  }

  public String getMentorNumComments() {
    return mentorNumComments;
  }

  public void setMentorNumComments(String mentorNumComments) {
    this.mentorNumComments = mentorNumComments;
  }

  public String getMentorDescription() {
    return mentorDescription;
  }

  public void setMentorDescription(String mentorDescription) {
    this.mentorDescription = mentorDescription;
  }

  public List<MentorsSkills> getMentorSkills() {
    return mentorSkills;
  }

  public void setMentorSkills(List<MentorsSkills> mentorSkills) {
    this.mentorSkills = mentorSkills;
  }

  public List<MemberOfMentorsGroup> getMembersOfMentorsGroup() {
    return membersOfMentorsGroup;
  }

  public void setMembersOfMentorsGroup(List<MemberOfMentorsGroup> membersOfMentorsGroup) {
    this.membersOfMentorsGroup = membersOfMentorsGroup;
  }
}
