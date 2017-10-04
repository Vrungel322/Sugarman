package com.sugarman.myb.listeners;

public interface ApiAddMembersListener extends ApiBaseListener {

  void onApiAddMembersSuccess();

  void onApiAddMembersFailure(String message);
}
