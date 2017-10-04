package com.sugarman.myb.eventbus.events;

public class DebugRequestStepsEvent {

  private final int startValue;

  public DebugRequestStepsEvent(int startValue) {
    this.startValue = startValue;
  }

  public int getStartValue() {
    return startValue;
  }
}
