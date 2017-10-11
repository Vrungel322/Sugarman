package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by yegoryeriomin on 10/11/17.
 */

public class CheckPhoneRequest {
  @SerializedName("phones") private List<String> phones;

  public void setPhones(List<String> phones) {
    this.phones = phones;
  }
}
