package com.sugarman.myb.models.tasks;

/**
 * Created by nikita on 24.10.2017.
 */

public class InAppDaysInRowTask extends Task {
  private int mDaysInARow;

  public InAppDaysInRowTask(int level, int id, int type, String name, String description) {
    super(level, id, type, name, description);
  }

  public InAppDaysInRowTask(int daysInARow, int level, int id, int type, String name,
      String description) {
    super(level, id, type, name, description);
    this.mDaysInARow = daysInARow;
  }
}
