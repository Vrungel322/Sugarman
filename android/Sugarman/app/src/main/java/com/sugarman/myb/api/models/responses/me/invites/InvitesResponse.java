package com.sugarman.myb.api.models.responses.me.invites;

import com.google.gson.annotations.SerializedName;

public class InvitesResponse {

  @SerializedName("result") private Invite[] result;

  public Invite[] getResult() {
    return result;
  }

  public void setResult(Invite[] result) {
    this.result = result;
  }
}
