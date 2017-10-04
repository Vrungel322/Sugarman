package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;

public class FacebookPicture {

  @SerializedName("data") private FacebookPictureData data;

  public FacebookPictureData getData() {
    return data;
  }

  public void setData(FacebookPictureData data) {
    this.data = data;
  }
}
