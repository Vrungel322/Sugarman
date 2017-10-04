package com.sugarman.myb.listeners;

public interface ApiManageInvitesListener extends ApiBaseListener {

  void onApiDeclineInviteSuccess();

  void onApiDeclineInviteFailure(String message);

  void onApiAcceptInviteSuccess();

  void onApiAcceptInviteFailure(String message);
}
