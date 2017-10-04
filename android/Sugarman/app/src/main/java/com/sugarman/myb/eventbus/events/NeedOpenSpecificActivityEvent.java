package com.sugarman.myb.eventbus.events;

public class NeedOpenSpecificActivityEvent {

  private final int activityCode;
  private final String trackingId;

  public NeedOpenSpecificActivityEvent(int activityCode, String trackingId) {
    this.activityCode = activityCode;
    this.trackingId = trackingId;
  }

  public int getActivityCode() {
    return activityCode;
  }

  public String getTrackingId() {
    return trackingId;
  }
}
