package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by nikita on 27.10.2017.
 */

public class MentorStupidAbstraction {
  @SerializedName("response")private List<MentorEntity> mMentorEntities;

  public MentorStupidAbstraction(List<MentorEntity> mentorEntities) {
    mMentorEntities = mentorEntities;
  }

  public void setMentorEntities(List<MentorEntity> mentorEntities) {
    mMentorEntities = mentorEntities;
  }

  public List<MentorEntity> getMentorEntities() {
    return mMentorEntities;
  }
}
