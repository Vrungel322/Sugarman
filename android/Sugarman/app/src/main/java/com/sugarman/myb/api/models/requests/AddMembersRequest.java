package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class AddMembersRequest {

  @SerializedName("members") private Member[] members;

  public Member[] getMembers() {
    return members;
  }

  public void setMembers(Member[] members) {
    this.members = members;
  }
}
