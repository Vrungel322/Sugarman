package com.sugarman.myb.api.models.responses.animation;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.models.animation.ImageModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yegoryeriomin on 11/29/17.
 */

public class GetAnimationResponse {
  @Getter @Setter @SerializedName("animations") List<ImageModel> animations;
}
