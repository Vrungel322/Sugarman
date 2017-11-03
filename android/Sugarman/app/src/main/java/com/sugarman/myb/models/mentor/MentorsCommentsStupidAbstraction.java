package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 31.10.2017.
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC) public class MentorsCommentsStupidAbstraction {
  @Getter @Setter @SerializedName("result") private List<MentorsCommentsEntity> mMentorsCommentsEntities;
}
