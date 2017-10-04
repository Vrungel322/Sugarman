package com.sugarman.myb.eventbus.events;

public class DebugRealStepAddedEvent {

  private final int stepsCalculated;

  public DebugRealStepAddedEvent(int stepsCalculated) {
    this.stepsCalculated = stepsCalculated;
  }

  public int getStepsCalculated() {
    return stepsCalculated;
  }
}
