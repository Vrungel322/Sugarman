package com.sugarman.myb.listeners;

public interface ApiSendFirebaseTokenListener extends ApiBaseListener {

  void onApiSendFirebaseTokenSuccess();

  void onApiSendFirebaseTokenFailure(String message);
}
