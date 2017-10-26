package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FacebookPictureData extends RealmObject {
  @PrimaryKey @SerializedName("id") private int id;

  @SerializedName("is_silhouette") private boolean isSilhouette;

  @SerializedName("url") private String url;

  public FacebookPictureData() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

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
