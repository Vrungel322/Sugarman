package com.sugarman.myb.models.chat_refactor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SeenBy {

  @SerializedName("user") @Expose private User_ user;
  //@SerializedName("at") @Expose private Integer at;

  public User_ getUser() {
    return user;
  }

  public void setUser(User_ user) {
    this.user = user;
  }
}
