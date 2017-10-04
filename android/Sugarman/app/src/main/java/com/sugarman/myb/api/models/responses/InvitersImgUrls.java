package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by nikita on 03.10.2017.
 */

public class InvitersImgUrls {
  @SerializedName("pictures") @Expose private List<String> pictures = null;

  public List<String> getPictures() {
    return pictures;
  }

  public void setPictures(List<String> pictures) {
    this.pictures = pictures;
  }
}
