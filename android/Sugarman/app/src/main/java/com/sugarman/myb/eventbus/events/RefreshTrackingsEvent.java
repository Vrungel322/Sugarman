package com.sugarman.myb.eventbus.events;

public class RefreshTrackingsEvent {

  private final String trackingId;

  public RefreshTrackingsEvent(String trackingId) {
    this.trackingId = trackingId;
  }

  public String getTrackingId() {
    return trackingId;
  }
}
