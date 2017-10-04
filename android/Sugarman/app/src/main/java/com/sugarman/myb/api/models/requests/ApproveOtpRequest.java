package com.sugarman.myb.api.models.requests;

import com.google.gson.annotations.SerializedName;

public class ApproveOtpRequest {

  @SerializedName("userId") private String userId;

  @SerializedName("phone_number") private String phoneNumber;

  @SerializedName("phone_otp") private String phoneOtp;

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setPhoneOtp(String phoneOtp) {
    this.phoneOtp = phoneOtp;
  }
}
