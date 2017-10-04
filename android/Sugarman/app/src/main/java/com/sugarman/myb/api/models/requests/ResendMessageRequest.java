package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class ResendMessageRequest {

  @SerializedName("phone_number") private String phoneNumber;

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
