package com.sugarman.myb.listeners;

@Deprecated
public interface ApiManageInvitesListener extends ApiBaseListener {

  void onApiDeclineInviteSuccess();

  void onApiDeclineInviteFailure(String message);

  void onApiAcceptInviteSuccess();

  void onApiAcceptInviteFailure(String message);
}
