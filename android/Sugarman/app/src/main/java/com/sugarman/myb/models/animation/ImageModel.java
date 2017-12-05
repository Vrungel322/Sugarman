package com.sugarman.myb.models.animation;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/28/17.
 */

@AllArgsConstructor @NoArgsConstructor public class ImageModel extends RealmObject {
  @PrimaryKey Integer id;
  @Getter @Setter @SerializedName("url") RealmList<String> imageUrl;
  @Getter @Setter @SerializedName("md5") RealmList<String> md5;
  @Getter @Setter @SerializedName("level") String level;
  @Getter @Setter @SerializedName("android_duration") Integer duration;
  @Getter @Setter @SerializedName("steps") Integer steps;

}
