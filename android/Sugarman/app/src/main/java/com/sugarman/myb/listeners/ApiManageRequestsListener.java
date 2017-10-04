package com.sugarman.myb.listeners;

public interface ApiManageRequestsListener extends ApiBaseListener {

  void onApiAcceptRequestSuccess();

  void onApiAcceptRequestFailure(String message);

  void onApiDeclineRequestSuccess();

  void onApiDeclineRequestFailure(String message);
}
