package com.sugarman.myb.eventbus.events;

public class ChallengeSelectedEvent {

  private final String id;

  public ChallengeSelectedEvent(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
