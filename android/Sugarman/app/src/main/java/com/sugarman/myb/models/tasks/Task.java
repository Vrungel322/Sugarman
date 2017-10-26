package com.sugarman.myb.models.tasks;

/**
 * Created by nikita on 24.10.2017.
 */

public abstract class Task {
  protected int mLevel;
  protected int mId;
  protected int mType;
  protected String mName;
  protected String mDescription;

  public Task(int level, int id, int type, String name, String discription) {
    mLevel = level;
    mId = id;
    mType = type;
    mName = name;
    mDescription = discription;
  }

  public int getLevel() {
    return mLevel;
  }

  public void setLevel(int level) {
    mLevel = level;
  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    mId = id;
  }

  public int getType() {
    return mType;
  }

  public void setType(int type) {
    mType = type;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

  public String getDescription() {
    return mDescription;
  }

  public void setDescription(String discription) {
    mDescription = discription;
  }
}
