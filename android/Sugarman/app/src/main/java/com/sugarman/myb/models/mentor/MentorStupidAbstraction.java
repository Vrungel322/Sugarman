package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 27.10.2017.
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC) public class MentorStupidAbstraction {
  @Getter @Setter @SerializedName("response") private List<MentorEntity> mentorEntities;
}
