package com.sugarman.myb.models.tasks;

/**
 * Created by nikita on 24.10.2017.
 */

public class CreateGroupTask extends Task {
  private boolean isOneGroupCreated;

  public CreateGroupTask(int level, int id, int type, String name, String discription) {
    super(level, id, type, name, discription);
  }

  public CreateGroupTask(boolean isOneGroupCreated, int level, int id, int type, String name,
      String discription) {
    super(level, id, type, name, discription);
    this.isOneGroupCreated = isOneGroupCreated;
  }
}
