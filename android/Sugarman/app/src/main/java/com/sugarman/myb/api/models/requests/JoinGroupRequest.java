package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class JoinGroupRequest {

  @SerializedName("group_id") private String groupId;

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
}
