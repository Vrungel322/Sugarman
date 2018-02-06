package com.sugarman.myb.models.custom_events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 29.11.2017.
 */
@NoArgsConstructor public class Rule extends RealmObject {
  @Getter @Setter @SerializedName("action") @Expose private String action;
  @Getter @Setter @SerializedName("action_type") @Expose private Integer ruleType;
  @Getter @Setter @SerializedName("count") @Expose private Integer count;
  @Getter @Setter @SerializedName("message") @Expose private String message;
  @Getter @Setter @SerializedName("name") @Expose private String name;
  @Getter @Setter @SerializedName("name_of_animation") @Expose private String nameOfAnim;
  @Getter @Setter @SerializedName("sequence") @Expose private Integer sequence;
  @Getter @Setter @SerializedName("group_count") @Expose private Integer groupCount;
  @Getter @Setter @SerializedName("pop_up_img") @Expose private String popUpImg;

  @Override public String toString() {
    return "  action = "
        + action
        + "   count = "
        + count
        + "   name_of_animation = "
        + nameOfAnim
        + "   group_count = "
        + groupCount;
  }
}
