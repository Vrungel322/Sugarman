package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class CheckPhoneResponse {

  @SerializedName("phones") private List<Phones> phones;

  public List<Phones> getPhones() {
    return phones;
  }
}
