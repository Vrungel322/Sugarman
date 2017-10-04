package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.ApproveOtpResponse;

public interface ApiApproveOtp extends ApiBaseListener {

  void onApiApproveOtpSuccess(ApproveOtpResponse response);

  void onApiApproveOtpFailure(String message);
}
