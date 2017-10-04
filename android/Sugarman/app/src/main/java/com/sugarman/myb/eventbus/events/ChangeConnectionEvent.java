package com.sugarman.myb.eventbus.events;

public class ChangeConnectionEvent {
  private final boolean isConnected;

  public ChangeConnectionEvent(boolean isConnected) {
    this.isConnected = isConnected;
  }

  public boolean isConnected() {
    return isConnected;
  }
}
