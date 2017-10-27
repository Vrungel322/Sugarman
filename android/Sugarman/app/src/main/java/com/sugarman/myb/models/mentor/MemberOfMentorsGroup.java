package com.sugarman.myb.models.mentor;

/**
 * Created by nikita on 27.10.2017.
 */

public class MemberOfMentorsGroup {
  private String name;
  private String imgUrl;

  public MemberOfMentorsGroup(String name, String imgUrl) {
    this.name = name;
    this.imgUrl = imgUrl;
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
}
