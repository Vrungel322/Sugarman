package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yegoryeriomin on 10/17/17.
 */

public class Phones {

  @SerializedName("fbid") String fbid;
  @SerializedName("phone") String phone;
  @SerializedName("vkid") String vkid;

  public String getVkid() {
    return vkid;
  }

  public String getFbid() {
    return fbid;
  }

  public String getPhone() {
    return phone;
  }
}
