package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.users.Result;
import com.sugarman.myb.api.models.responses.users.User;
import java.util.List;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class CheckPhoneResponse {

  @SerializedName("phones") private List<String> phones;

  public List<String> getPhones() {
    return phones;
  }
}
