package com.sugarman.myb.models.mentor;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikita on 27.10.2017.
 */
@AllArgsConstructor public class MentorEntity implements Parcelable {
  public static final String MENTOR_ENTITY = "MENTOR_ENTITY";

  @Getter @Setter @SerializedName("id") private String mentorId;
  @Getter @Setter @SerializedName("id_user") private String userId;
  @Getter @Setter @SerializedName("picture_url") private String mentorImgUrl;
  @Getter @Setter @SerializedName("name") private String mentorName;
  @Getter @Setter @SerializedName("rating") private String mentorRating;
  @Getter @Setter @SerializedName("mentor_num_comments") private String mentorNumComments;
  @Getter @Setter @SerializedName("description") private String mentorDescription;
  @Getter @Setter @SerializedName("skills") private List<MentorsSkills> mentorSkills;
  @Getter @Setter @SerializedName("members_statuses") private List<MemberOfMentorsGroup>
      membersOfMentorsGroup;
  @Getter @Setter @SerializedName("youtube_videos") private List<String> youtubeVideos;
  @Getter @Setter @SerializedName("assessment_of_day") private float dailySuccessRate;
  @Getter @Setter @SerializedName("assessment_of_week") private float weeklySuccessRate;
  @Getter @Setter @SerializedName("assessment_of_month") private float monthlySuccessRate;
  @Getter @Setter @SerializedName("is_already_owned") private boolean isOwned;
  @Getter @Setter private Float price;

  protected MentorEntity(Parcel in) {
    mentorId = in.readString();
    userId = in.readString();
    mentorImgUrl = in.readString();
    mentorName = in.readString();
    mentorRating = in.readString();
    mentorNumComments = in.readString();
    mentorDescription = in.readString();
    mentorSkills = in.createTypedArrayList(MentorsSkills.CREATOR);
    membersOfMentorsGroup = in.createTypedArrayList(MemberOfMentorsGroup.CREATOR);
    youtubeVideos = in.createStringArrayList();
    dailySuccessRate = in.readFloat();
    weeklySuccessRate = in.readFloat();
    monthlySuccessRate = in.readFloat();
    isOwned = in.readByte() != 0;
    if (in.readByte() == 0) {
      price = null;
    } else {
      price = in.readFloat();
    }
  }

  public static final Creator<MentorEntity> CREATOR = new Creator<MentorEntity>() {
    @Override public MentorEntity createFromParcel(Parcel in) {
      return new MentorEntity(in);
    }

    @Override public MentorEntity[] newArray(int size) {
      return new MentorEntity[size];
    }
  };

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(mentorId);
    parcel.writeString(userId);
    parcel.writeString(mentorImgUrl);
    parcel.writeString(mentorName);
    parcel.writeString(mentorRating);
    parcel.writeString(mentorNumComments);
    parcel.writeString(mentorDescription);
    parcel.writeTypedList(mentorSkills);
    parcel.writeTypedList(membersOfMentorsGroup);
    parcel.writeStringList(youtubeVideos);
    parcel.writeFloat(dailySuccessRate);
    parcel.writeFloat(weeklySuccessRate);
    parcel.writeFloat(monthlySuccessRate);
    parcel.writeByte((byte) (isOwned ? 1 : 0));
    if (price == null) {
      parcel.writeByte((byte) 0);
    } else {
      parcel.writeByte((byte) 1);
      parcel.writeFloat(price);
    }
  }
}
