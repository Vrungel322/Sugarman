package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.me.invites.Invite;

public interface OnInvitesActionListener {

  void onDeclineInvite(Invite invite, int position);

  void onAcceptInvite(Invite invite, int position);
}
