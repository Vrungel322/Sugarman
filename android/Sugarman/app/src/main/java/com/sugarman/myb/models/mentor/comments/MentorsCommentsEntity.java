package com.sugarman.myb.models.mentor.comments;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 31.10.2017.
 */
@AllArgsConstructor @NoArgsConstructor public class MentorsCommentsEntity extends CommentEntity {
  @SerializedName("name") @Getter @Setter private String authorsName;
  @SerializedName("img_url") @Getter @Setter private String authorsImg;

  public MentorsCommentsEntity(String authorsRating, String authorsComment, String authorsName,
      String authorsImg) {
    super(authorsRating, authorsComment);
    this.authorsName = authorsName;
    this.authorsImg = authorsImg;
  }
}
