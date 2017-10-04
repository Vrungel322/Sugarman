package com.sugarman.myb.eventbus.events;

public class InviteRemovedEvent {

  private final String id;

  public InviteRemovedEvent(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
