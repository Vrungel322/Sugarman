package com.sugarman.myb.models.animation;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/28/17.
 */

@AllArgsConstructor @NoArgsConstructor public class ImageModel {
  @Getter @Setter @SerializedName("url") List<String> imageUrl;
  @Getter @Setter @SerializedName("md5") List<String> md5;
  @Getter @Setter @SerializedName("level") String level;
  @Getter @Setter @SerializedName("android_duration") int duration;
  @Getter @Setter @SerializedName("steps") int steps;

}
