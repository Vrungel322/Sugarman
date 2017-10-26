package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class CheckVkResponse {

  @SerializedName("vks") private List<String> vks;

  public List<String> getVks() {
    return vks;
  }
}
