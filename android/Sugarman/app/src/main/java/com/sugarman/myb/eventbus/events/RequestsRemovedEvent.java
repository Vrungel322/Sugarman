package com.sugarman.myb.eventbus.events;

public class RequestsRemovedEvent {

  private final String id;

  public RequestsRemovedEvent(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
