package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by nikita on 27.10.2017.
 */

public class MentorsSkills {
  @SerializedName("title")private String skillTitle;
  @SerializedName("skills")private List<String> skills;
  @SerializedName("skill_left_icon")private String skillLeftIcon;
  @SerializedName("skill_right_icon")private String skillRightIcon;

  public MentorsSkills(String skillTitle, List<String> skills, String skillLeftIcon,
      String skillRightIcon) {
    this.skillTitle = skillTitle;
    this.skills = skills;
    this.skillLeftIcon = skillLeftIcon;
    this.skillRightIcon = skillRightIcon;
  }

  public String getSkillTitle() {
    return skillTitle;
  }

  public void setSkillTitle(String skillTitle) {
    this.skillTitle = skillTitle;
  }

  public List<String> getSkills() {
    return skills;
  }

  public void setSkills(List<String> skills) {
    this.skills = skills;
  }

  public String getSkillLeftIcon() {
    return skillLeftIcon;
  }

  public void setSkillLeftIcon(String skillLeftIcon) {
    this.skillLeftIcon = skillLeftIcon;
  }

  public String getSkillRightIcon() {
    return skillRightIcon;
  }

  public void setSkillRightIcon(String skillRightIcon) {
    this.skillRightIcon = skillRightIcon;
  }
}
