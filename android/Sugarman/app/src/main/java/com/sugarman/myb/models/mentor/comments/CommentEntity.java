package com.sugarman.myb.models.mentor.comments;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 03.11.2017.
 */
@AllArgsConstructor @NoArgsConstructor public class CommentEntity {
  @SerializedName("rating") @Getter @Setter private String authorsRating;
  @SerializedName("comment") @Getter @Setter private String authorsComment;
}
