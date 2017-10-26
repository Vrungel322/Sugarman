package com.sugarman.myb.models.tasks;

/**
 * Created by nikita on 24.10.2017.
 */

public class StepsPerDayTask extends Task {
  private int mNumberOfStepsPerDay;
  private int mNumOfDays;

  public StepsPerDayTask(int level, int id, int type, String name, String description) {
    super(level, id, type, name, description);
  }

  public StepsPerDayTask(int numberOfStepsPerDay, int numOfDays, int level, int id, int type,
      String name, String description) {
    super(level, id, type, name, description);
    this.mNumberOfStepsPerDay = numberOfStepsPerDay;
    this.mNumOfDays = numOfDays;
  }
}
