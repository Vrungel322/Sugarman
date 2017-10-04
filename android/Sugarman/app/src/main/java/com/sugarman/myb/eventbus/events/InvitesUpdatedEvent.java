package com.sugarman.myb.eventbus.events;

import com.sugarman.myb.api.models.responses.me.invites.Invite;
import java.util.ArrayList;
import java.util.List;

public class InvitesUpdatedEvent {

  private final List<Invite> invites = new ArrayList<>(0);

  public InvitesUpdatedEvent(List<Invite> invites) {
    this.invites.clear();
    this.invites.addAll(invites);
  }

  public List<Invite> getInvites() {
    return invites;
  }
}
