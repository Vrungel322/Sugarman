package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by yegoryeriomin on 10/11/17.
 */

public class CheckVkRequest {
  @SerializedName("vks") private List<String> vks;

  public void setVks(List<String> vks) {
    this.vks = vks;
  }
}
