package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;

public class FacebookPictureData {

  @SerializedName("is_silhouette") private boolean isSilhouette;

  @SerializedName("url") private String url;

  public boolean isSilhouette() {
    return isSilhouette;
  }

  public void setSilhouette(boolean silhouette) {
    isSilhouette = silhouette;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
