package com.sugarman.myb.models.mentor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 31.10.2017.
 */
@AllArgsConstructor @NoArgsConstructor public class MentorsCommentsEntity {
  @Getter @Setter private String authorsName;
  @Getter @Setter private String authorsImg;
  @Getter @Setter private String authorsRating;
  @Getter @Setter private String authorsComment;
}
