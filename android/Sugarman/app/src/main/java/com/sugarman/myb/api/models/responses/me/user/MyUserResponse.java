package com.sugarman.myb.api.models.responses.me.user;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.users.User;

public class MyUserResponse {

  @SerializedName("result") private User result;

  public User getResult() {
    return result;
  }

  public void setResult(User result) {
    this.result = result;
  }
}
