package com.sugarman.myb.api.models.responses.users;

import com.google.gson.annotations.SerializedName;

public class UsersResponse {

  @SerializedName("result") private Result result;

  @SerializedName("tokens") private Tokens tokens;

  @SerializedName("user") private User user;

  @SerializedName("error") private String error;

  public String getError() {
    return error;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

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

  @Override public String toString() {
    return user.getName();
  }
}
