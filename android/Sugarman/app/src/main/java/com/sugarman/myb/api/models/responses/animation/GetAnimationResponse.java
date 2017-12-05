package com.sugarman.myb.api.models.responses.animation;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.models.animation.ImageModel;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/29/17.
 */

public class GetAnimationResponse extends RealmObject {
  @PrimaryKey Integer id;
  @Getter @Setter @SerializedName("animations") RealmList<ImageModel> animations;
}
