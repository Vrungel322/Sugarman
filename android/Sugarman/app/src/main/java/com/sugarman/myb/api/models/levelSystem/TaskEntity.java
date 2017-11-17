package com.sugarman.myb.api.models.levelSystem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TaskEntity {

  @SerializedName("tasks") @Expose private List<Task> tasks = null;

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }
}
