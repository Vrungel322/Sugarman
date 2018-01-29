package com.sugarman.myb.models.chat_refactor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

  @SerializedName("_id") @Expose private String id;
  @SerializedName("userID") @Expose private String userID;
  @SerializedName("name") @Expose private String name;
  @SerializedName("avatarURL") @Expose private String avatarURL;
  @SerializedName("token") @Expose private String token;
  //@SerializedName("created") @Expose private Integer created;
  //@SerializedName("__v") @Expose private Integer v;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatarURL() {
    return avatarURL;
  }

  public void setAvatarURL(String avatarURL) {
    this.avatarURL = avatarURL;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
