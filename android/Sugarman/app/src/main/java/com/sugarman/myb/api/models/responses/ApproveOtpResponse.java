package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.users.Result;
import com.sugarman.myb.api.models.responses.users.User;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class ApproveOtpResponse {

  @SerializedName("result") private Result result;

  @SerializedName("code") private String code;

  @SerializedName("user") private User user;

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String tokens) {
    this.code = tokens;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override public String toString() {
    return user.getName();
  }
}
