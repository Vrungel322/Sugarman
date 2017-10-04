package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class RefreshUserDataRequest {

  @SerializedName("token") private String token;

  @SerializedName("timezone") private String timezone;

  @SerializedName("vk_token") private String vkToken;

  @SerializedName("g_token") private String gToken;

  @SerializedName("phone_number") private String phoneNumber;

  @SerializedName("phone_token") private String phoneToken;

  @SerializedName("fbid") private String fbId;

  @SerializedName("vkid") private String vkId;

  @SerializedName("googleid") private String googleId;

  @SerializedName("email") private String email;

  @SerializedName("link") private String link;

  @SerializedName("name") private String name;

  @SerializedName("vk_link") private String vkLink;

  @SerializedName("picture") private String pictureUrl;

  public void setPhoneToken(String phoneToken) {
    this.phoneToken = phoneToken;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVkLink(String vkLink) {
    this.vkLink = vkLink;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setGoogleId(String googleId) {
    this.googleId = googleId;
  }

  public void setVkId(String vkId) {
    this.vkId = vkId;
  }

  public void setFbId(String fbId) {
    this.fbId = fbId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setVkToken(String vkToken) {
    this.vkToken = vkToken;
  }

  public String getVKToken() {
    return vkToken;
  }

  public void setgToken(String gToken) {
    this.gToken = gToken;
  }

  public String getgToken() {
    return gToken;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}
