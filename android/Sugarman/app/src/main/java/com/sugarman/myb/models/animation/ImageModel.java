package com.sugarman.myb.models.animation;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/28/17.
 */

@AllArgsConstructor @NoArgsConstructor public class ImageModel {
  @Getter @Setter @SerializedName("url") String imageUrl;
  @Getter @Setter @SerializedName("md5") String md5;
  @Getter @Setter @SerializedName("index") String index ;

}
