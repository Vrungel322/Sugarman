package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class Member {

  @SerializedName("name") private String name;

  @SerializedName("fbid") private String fbid;

  @SerializedName("vkid") private String vkid;

  @SerializedName("picture_url") private String pictureUrl;

  public String getName() {
    return name;
  }

  public String getVkid() {
    return vkid;
  }

  public void setVkid(String vkid) {
    this.vkid = vkid;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFbid() {
    return fbid;
  }

  public void setFbid(String fbid) {
    this.fbid = fbid;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }
}
