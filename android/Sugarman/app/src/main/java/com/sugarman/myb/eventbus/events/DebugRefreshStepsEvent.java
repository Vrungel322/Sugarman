package com.sugarman.myb.eventbus.events;

public class DebugRefreshStepsEvent {

  private final int realSteps;

  private final int cacheSteps;

  private final int startValue;

  public DebugRefreshStepsEvent(int realSteps, int cacheSteps, int startValue) {
    this.realSteps = realSteps;
    this.cacheSteps = cacheSteps;
    this.startValue = startValue;
  }

  public int getRealSteps() {
    return realSteps;
  }

  public int getCacheSteps() {
    return cacheSteps;
  }

  public int getStartValue() {
    return startValue;
  }
}
