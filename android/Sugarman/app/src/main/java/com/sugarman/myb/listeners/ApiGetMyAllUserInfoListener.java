package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.AllMyUserDataResponse;

public interface ApiGetMyAllUserInfoListener extends ApiBaseListener {

  void onApiGetMyAllUserInfoSuccess(AllMyUserDataResponse allMyInfo);

  void onApiGetMyAllUserInfoFailure(String message);

  void onApiGetMyAllUserInfoNeedApproveOTP(String phone);
}