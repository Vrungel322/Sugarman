package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 31.10.2017.
 */
@AllArgsConstructor @NoArgsConstructor public class MentorsCommentsEntity {
  @SerializedName("name") @Getter @Setter private String authorsName;
  @SerializedName("img_url") @Getter @Setter private String authorsImg;
  @SerializedName("rating")@Getter @Setter private String authorsRating;
  @SerializedName("comment") @Getter @Setter private String authorsComment;
}
