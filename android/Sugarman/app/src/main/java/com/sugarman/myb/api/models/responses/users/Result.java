package com.sugarman.myb.api.models.responses.users;

import com.google.gson.annotations.SerializedName;

public class Result {

  @SerializedName("tokens") private Tokens tokens;

  @SerializedName("user") private User user;

  public Tokens getTokens() {
    return tokens;
  }

  public void setTokens(Tokens tokens) {
    this.tokens = tokens;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
