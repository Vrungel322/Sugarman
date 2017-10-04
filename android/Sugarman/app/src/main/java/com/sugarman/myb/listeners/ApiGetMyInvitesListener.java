package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.invites.Invite;

public interface ApiGetMyInvitesListener extends ApiBaseListener {

  void onApiGetMyInvitesSuccess(Invite[] invites, boolean isRefreshTrackings);

  void onApiGetMyInvitesFailure(String message);
}
