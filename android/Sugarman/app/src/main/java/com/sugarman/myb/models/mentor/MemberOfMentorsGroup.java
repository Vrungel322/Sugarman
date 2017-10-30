package com.sugarman.myb.models.mentor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nikita on 27.10.2017.
 */

public class MemberOfMentorsGroup {
  @SerializedName("name")private String name;
  @SerializedName("img_url") private String imgUrl;
  @SerializedName("id_member") private String idMember;

  public MemberOfMentorsGroup(String name, String imgUrl, String idMember) {
    this.name = name;
    this.imgUrl = imgUrl;
    this.idMember = idMember;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getIdMember() {
    return idMember;
  }

  public void setIdMember(String idMember) {
    this.idMember = idMember;
  }
}
