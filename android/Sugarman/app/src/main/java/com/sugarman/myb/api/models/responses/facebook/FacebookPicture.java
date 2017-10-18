package com.sugarman.myb.api.models.responses.facebook;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FacebookPicture extends RealmObject {

  @PrimaryKey @SerializedName("id") private int id;

  @SerializedName("data") private FacebookPictureData data;

  public FacebookPicture() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public FacebookPictureData getData() {
    return data;
  }

  public void setData(FacebookPictureData data) {
    this.data = data;
  }
}
