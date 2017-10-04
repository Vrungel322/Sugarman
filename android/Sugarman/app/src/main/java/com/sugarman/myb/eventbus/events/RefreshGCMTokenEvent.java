package com.sugarman.myb.eventbus.events;

public class RefreshGCMTokenEvent {

  private final String token;

  public RefreshGCMTokenEvent(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}